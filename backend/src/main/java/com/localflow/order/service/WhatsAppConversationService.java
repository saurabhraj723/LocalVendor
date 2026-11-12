package com.localflow.order.service;

import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.catalogue.domain.Product;
import com.localflow.catalogue.repository.ProductRepository;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUserProvider;
import com.localflow.customer.domain.Customer;
import com.localflow.customer.domain.CustomerAddress;
import com.localflow.customer.repository.CustomerRepository;
import com.localflow.order.api.dto.CreateWhatsAppOrderRequest;
import com.localflow.order.api.dto.OrderItemRequest;
import com.localflow.order.api.dto.OrderResponse;
import com.localflow.order.api.dto.WhatsAppConversationMessageRequest;
import com.localflow.order.api.dto.WhatsAppConversationResponse;
import com.localflow.order.api.dto.WhatsAppConversationStartRequest;
import com.localflow.order.domain.WhatsAppTestConversation;
import com.localflow.order.domain.WhatsAppTestConversationState;
import com.localflow.order.domain.WhatsAppTestCartItem;
import com.localflow.order.domain.WhatsAppTestMessage;
import com.localflow.order.repository.WhatsAppTestCartItemRepository;
import com.localflow.order.repository.WhatsAppTestConversationRepository;
import com.localflow.order.repository.WhatsAppTestMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class WhatsAppConversationService {

    private final WhatsAppTestConversationRepository conversationRepository;
    private final WhatsAppTestMessageRepository messageRepository;
    private final WhatsAppTestCartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final CurrentUserProvider currentUserProvider;
    private final OrderService orderService;

    public WhatsAppConversationService(
            WhatsAppTestConversationRepository conversationRepository,
            WhatsAppTestMessageRepository messageRepository,
            WhatsAppTestCartItemRepository cartItemRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            BusinessRepository businessRepository,
            CurrentUserProvider currentUserProvider,
            OrderService orderService
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.currentUserProvider = currentUserProvider;
        this.orderService = orderService;
    }

    @Transactional
    public WhatsAppConversationResponse start(WhatsAppConversationStartRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        Optional<WhatsAppTestMessage> replay = messageRepository.findByBusiness_IdAndMessageId(businessId, request.messageId());
        if (replay.isPresent()) return fromMessage(replay.get(), true);

        String phone = normalizePhone(request.customerPhone());
        List<Customer> matches = customerRepository.findAllByBusiness_IdOrderByNameAsc(businessId).stream()
                .filter(customer -> normalizePhone(customer.getPhone()).equals(phone))
                .toList();
        if (matches.isEmpty()) {
            throw new NotFoundException("CUSTOMER_PHONE_NOT_FOUND", "No customer matches that phone number in this business.");
        }
        if (matches.size() > 1) {
            throw new ConflictException("CUSTOMER_PHONE_AMBIGUOUS", "More than one customer matches that phone number. Update the customer records before continuing.");
        }
        Customer customer = matches.getFirst();
        Business business = businessRepository.getReferenceById(businessId);
        List<CustomerAddress> addresses = customer.getAddresses();
        if (addresses.isEmpty()) {
            throw new ConflictException("CUSTOMER_ADDRESS_REQUIRED", "This customer has no saved delivery address. Add an address before starting the order.");
        }
        WhatsAppTestConversation conversation = new WhatsAppTestConversation();
        conversation.setBusiness(business);
        conversation.setCustomer(customer);
        List<CustomerAddress> defaults = addresses.stream().filter(CustomerAddress::isDefaultAddress).toList();
        if (!defaults.isEmpty()) {
            conversation.setSelectedAddressId(defaults.getFirst().getId());
            conversation.setState(WhatsAppTestConversationState.AWAITING_ADDRESS_CONFIRMATION);
        } else if (addresses.size() == 1) {
            conversation.setSelectedAddressId(addresses.getFirst().getId());
            conversation.setState(WhatsAppTestConversationState.AWAITING_ADDRESS_CONFIRMATION);
        } else {
            conversation.setState(WhatsAppTestConversationState.SELECTING_ADDRESS);
        }
        conversation = conversationRepository.save(conversation);

        String reply = addressSelectionReply(conversation, customer, addresses);
        return record(request.messageId(), conversation, reply);
    }

    @Transactional
    public WhatsAppConversationResponse message(Long conversationId, WhatsAppConversationMessageRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        Optional<WhatsAppTestMessage> replay = messageRepository.findByBusiness_IdAndMessageId(businessId, request.messageId());
        if (replay.isPresent()) return fromMessage(replay.get(), true);

        WhatsAppTestConversation conversation = conversationRepository.findByIdAndBusiness_Id(conversationId, businessId)
                .orElseThrow(() -> new NotFoundException("CONVERSATION_NOT_FOUND", "Conversation was not found."));
        String text = request.text().trim();
        String normalized = text.toLowerCase(Locale.ROOT);
        String reply;

        if (normalized.equals("cancel") || normalized.equals("stop")) {
            conversation.setState(WhatsAppTestConversationState.CANCELLED);
            reply = "Order cancelled. Start a new conversation whenever you are ready.";
        } else {
            reply = switch (conversation.getState()) {
                case SELECTING_ADDRESS -> selectAddress(conversation, text);
                case AWAITING_ADDRESS_CONFIRMATION -> confirmAddress(conversation, normalized);
                case SELECTING_PRODUCT -> selectProduct(conversation, businessId, text);
                case AWAITING_QUANTITY -> setQuantity(conversation, businessId, text);
                case AWAITING_CONFIRMATION -> confirm(conversation, request.messageId(), normalized);
                case COMPLETED -> "This order is already confirmed as " + conversation.getOrderNumber() + ". Start a new conversation for another order.";
                case CANCELLED -> "This conversation was cancelled. Start a new conversation to place an order.";
            };
        }

        conversationRepository.save(conversation);
        return record(request.messageId(), conversation, reply);
    }

    private String addressSelectionReply(
            WhatsAppTestConversation conversation,
            Customer customer,
            List<CustomerAddress> addresses
    ) {
        if (conversation.getState() == WhatsAppTestConversationState.SELECTING_ADDRESS) {
            StringBuilder reply = new StringBuilder("Choose a delivery address by replying with its address ID:\n");
            for (CustomerAddress address : addresses) {
                reply.append(address.getId()).append(". ").append(formatAddress(address)).append('\n');
            }
            return reply.toString().trim();
        }
        return "Hi " + customer.getName() + ". Deliver to this address?\n" + selectedAddressText(conversation)
                + "\nReply YES to confirm, CHANGE to choose another saved address, or CANCEL.";
    }

    private String selectAddress(WhatsAppTestConversation conversation, String text) {
        Long addressId;
        try {
            addressId = Long.parseLong(text);
        } catch (NumberFormatException exception) {
            return "Reply with one of the address IDs listed above, or CANCEL.";
        }
        CustomerAddress address = conversation.getCustomer().getAddresses().stream()
                .filter(candidate -> candidate.getId().equals(addressId)).findFirst().orElse(null);
        if (address == null) return "That address does not belong to this customer. Reply with a listed address ID.";
        conversation.setSelectedAddressId(address.getId());
        conversation.setState(WhatsAppTestConversationState.AWAITING_ADDRESS_CONFIRMATION);
        return "Deliver to this address?\n" + formatAddress(address)
                + "\nReply YES to confirm, CHANGE to choose another saved address, or CANCEL.";
    }

    private String confirmAddress(WhatsAppTestConversation conversation, String normalized) {
        if (normalized.equals("change")) {
            List<CustomerAddress> addresses = conversation.getCustomer().getAddresses();
            if (addresses.size() == 1) return "Only one saved address is available. Reply YES to confirm it, or CANCEL.";
            conversation.setState(WhatsAppTestConversationState.SELECTING_ADDRESS);
            return addressSelectionReply(conversation, conversation.getCustomer(), addresses);
        }
        if (!normalized.equals("yes") && !normalized.equals("y")) {
            return "Please reply YES to confirm this delivery address, CHANGE to choose another, or CANCEL.";
        }
        conversation.setState(WhatsAppTestConversationState.SELECTING_PRODUCT);
        return catalogReply(currentUserProvider.currentBusinessId());
    }

    private String selectedAddressText(WhatsAppTestConversation conversation) {
        return conversation.getCustomer().getAddresses().stream()
                .filter(address -> address.getId().equals(conversation.getSelectedAddressId()))
                .findFirst().map(this::formatAddress).orElse("Saved address unavailable.");
    }

    private String formatAddress(CustomerAddress address) {
        StringBuilder text = new StringBuilder();
        if (address.getRecipientName() != null && !address.getRecipientName().isBlank()) {
            text.append(address.getRecipientName()).append(" - ");
        }
        text.append(address.getAddressLine1());
        if (address.getAddressLine2() != null && !address.getAddressLine2().isBlank()) text.append(", ").append(address.getAddressLine2());
        if (address.getLocality() != null && !address.getLocality().isBlank()) text.append(", ").append(address.getLocality());
        text.append(", ").append(address.getCity());
        if (address.getState() != null && !address.getState().isBlank()) text.append(", ").append(address.getState());
        if (address.getPostalCode() != null && !address.getPostalCode().isBlank()) text.append(" - ").append(address.getPostalCode());
        return text.toString();
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private String catalogReply(Long businessId) {
        List<Product> products = productRepository.findAllByBusiness_IdAndActiveTrueOrderByNameAsc(businessId)
                .stream().filter(Product::isAvailable).toList();
        if (products.isEmpty()) return "There are no available products right now. Reply REVIEW to check your cart or CANCEL to stop.";

        StringBuilder reply = new StringBuilder("Available products:\n");
        for (Product product : products) {
            BigDecimal discountedPrice = product.getPrice().multiply(new BigDecimal("100").subtract(product.getDiscountPercent()))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            reply.append(product.getId()).append(". ").append(product.getName())
                    .append(" - ").append(discountedPrice);
            if (product.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                reply.append(" (MRP ").append(product.getPrice().setScale(2, RoundingMode.HALF_UP))
                        .append(", ").append(product.getDiscountPercent()).append("% off)");
            }
            if (product.getTaxType() != null) reply.append(" + ").append(product.getTaxPercent()).append("% ").append(product.getTaxType());
            reply
                    .append(" per ").append(product.getUnit()).append('\n');
        }
        return reply.append("Reply with a product ID to add it, REVIEW to check your cart, or CANCEL to stop.").toString();
    }

    private String selectProduct(WhatsAppTestConversation conversation, Long businessId, String text) {
        String normalized = text.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("review")) return reviewCart(conversation, businessId);
        if (normalized.startsWith("remove ")) return removeCartItem(conversation, normalized.substring(7).trim());

        Long productId;
        try {
            productId = Long.parseLong(text);
        } catch (NumberFormatException exception) {
            return "Please reply with one of the product IDs listed above, or CANCEL.";
        }

        Product product = productRepository.findByIdAndBusiness_IdAndActiveTrue(productId, businessId)
                .filter(Product::isAvailable)
                .orElse(null);
        if (product == null) return "That product is unavailable. Reply with an available product ID, or CANCEL.";
        if (cartItemRepository.findByConversation_IdAndProduct_Id(conversation.getId(), productId).isPresent()) {
            return "That product is already in your cart. Reply REVIEW to see the cart, REMOVE " + productId
                    + " to remove it, or choose a different product.";
        }

        conversation.setProductId(product.getId());
        conversation.setQuotedUnitPrice(product.getPrice());
        conversation.setState(WhatsAppTestConversationState.AWAITING_QUANTITY);
        return "How much " + product.getName() + " would you like? Minimum: " + product.getMinQuantity()
                + " " + product.getUnit() + "; quantity must increase in steps of " + product.getQuantityStep() + " " + product.getUnit() + ".";
    }

    private String setQuantity(WhatsAppTestConversation conversation, Long businessId, String text) {
        Product product = availableSelectedProduct(conversation, businessId);
        if (product == null) {
            conversation.setState(WhatsAppTestConversationState.SELECTING_PRODUCT);
            return catalogReply(businessId) + "\nThe previously selected product is no longer available.";
        }

        BigDecimal quantity;
        try {
            quantity = new BigDecimal(text);
        } catch (NumberFormatException exception) {
            return "Please enter a valid quantity as a number, such as 2 or 0.5.";
        }

        if (quantity.compareTo(product.getMinQuantity()) < 0
                || quantity.subtract(product.getMinQuantity()).remainder(product.getQuantityStep()).compareTo(BigDecimal.ZERO) != 0) {
            return "That quantity is not valid. Minimum is " + product.getMinQuantity() + " and the step is "
                    + product.getQuantityStep() + " " + product.getUnit() + ". Please enter another quantity.";
        }

        conversation.setQuantity(quantity);
        conversation.setQuotedUnitPrice(product.getPrice());
        WhatsAppTestCartItem cartItem = new WhatsAppTestCartItem();
        cartItem.setConversation(conversation);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setQuotedUnitPrice(product.getPrice());
        cartItem.setQuotedDiscountPercent(product.getDiscountPercent());
        cartItem.setQuotedTaxType(product.getTaxType());
        cartItem.setQuotedTaxPercent(product.getTaxPercent());
        cartItemRepository.save(cartItem);
        conversation.setProductId(null);
        conversation.setQuantity(null);
        conversation.setQuotedUnitPrice(null);
        conversation.setState(WhatsAppTestConversationState.SELECTING_PRODUCT);
        return quantity.stripTrailingZeros().toPlainString() + " " + product.getUnit() + " of " + product.getName()
                + " added to your cart. Reply with another product ID, REVIEW to see your cart, or CANCEL to stop.";
    }

    private String confirm(WhatsAppTestConversation conversation, String messageId, String normalized) {
        if (normalized.equals("no") || normalized.equals("cancel")) {
            conversation.setState(WhatsAppTestConversationState.CANCELLED);
            return "Order cancelled. No order was created.";
        }
        if (!normalized.equals("yes") && !normalized.equals("y")) {
            return "Please reply YES to place the order or NO to cancel.";
        }

        List<WhatsAppTestCartItem> cart = cartItemRepository.findAllByConversation_IdOrderByIdAsc(conversation.getId());
        if (cart.isEmpty()) {
            conversation.setState(WhatsAppTestConversationState.SELECTING_PRODUCT);
            return "Your cart is empty. Choose a product before confirming.\n" + catalogReply(currentUserProvider.currentBusinessId());
        }
        boolean priceChanged = false;
        for (WhatsAppTestCartItem item : cart) {
            Product currentProduct = availableSelectedProduct(conversation, item.getProduct().getId(), currentUserProvider.currentBusinessId());
            if (currentProduct == null) {
                conversation.setState(WhatsAppTestConversationState.SELECTING_PRODUCT);
                return "A product in your cart is no longer available. Reply REVIEW to inspect it or REMOVE <productId> to remove it.";
            }
            if (currentProduct.getPrice().compareTo(item.getQuotedUnitPrice()) != 0
                    || !sameAmount(currentProduct.getDiscountPercent(), item.getQuotedDiscountPercent())
                    || !java.util.Objects.equals(currentProduct.getTaxType(), item.getQuotedTaxType())
                    || !sameAmount(currentProduct.getTaxPercent(), item.getQuotedTaxPercent())) {
                item.setQuotedUnitPrice(currentProduct.getPrice());
                priceChanged = true;
            }
        }
        if (priceChanged) return reviewCart(conversation, currentUserProvider.currentBusinessId())
                + "\nA product price, discount or tax changed. Reply YES to accept the updated total or NO to cancel.";

        OrderResponse order = orderService.createFromWhatsAppTest(new CreateWhatsAppOrderRequest(
                messageId,
                conversation.getCustomer().getId(),
                cart.stream().map(item -> new OrderItemRequest(item.getProduct().getId(), item.getQuantity())).toList(),
                "Customer order via WhatsApp",
                conversation.getSelectedAddressId()
        )).order();
        conversation.setOrderId(order.id());
        conversation.setOrderNumber(order.orderNumber());
        conversation.setState(WhatsAppTestConversationState.COMPLETED);
        return "Order placed successfully. Order number: " + order.orderNumber() + ". Total: "
                + order.totalAmount().setScale(2, RoundingMode.HALF_UP) + " " + order.currency() + ".";
    }

    private Product availableSelectedProduct(WhatsAppTestConversation conversation, Long businessId) {
        return availableSelectedProduct(conversation, conversation.getProductId(), businessId);
    }

    private Product availableSelectedProduct(WhatsAppTestConversation conversation, Long productId, Long businessId) {
        if (productId == null) return null;
        return productRepository.findByIdAndBusiness_IdAndActiveTrue(productId, businessId)
                .filter(Product::isAvailable).orElse(null);
    }

    private String reviewCart(WhatsAppTestConversation conversation, Long businessId) {
        List<WhatsAppTestCartItem> cart = cartItemRepository.findAllByConversation_IdOrderByIdAsc(conversation.getId());
        if (cart.isEmpty()) return "Your cart is empty. Choose an available product ID to add it.";
        BigDecimal total = BigDecimal.ZERO;
        StringBuilder reply = new StringBuilder("Your cart:\n");
        for (WhatsAppTestCartItem item : cart) {
            Product product = availableSelectedProduct(conversation, item.getProduct().getId(), businessId);
            if (product == null) {
                reply.append("Unavailable product ID ").append(item.getProduct().getId()).append(" (remove it with REMOVE ")
                        .append(item.getProduct().getId()).append(")\n");
                continue;
            }
            item.setQuotedUnitPrice(product.getPrice());
            item.setQuotedDiscountPercent(product.getDiscountPercent());
            item.setQuotedTaxType(product.getTaxType());
            item.setQuotedTaxPercent(product.getTaxPercent());
            BigDecimal grossTotal = product.getPrice().multiply(item.getQuantity()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal discount = grossTotal.multiply(product.getDiscountPercent()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = grossTotal.subtract(discount);
            BigDecimal tax = product.getTaxPercent() == null ? BigDecimal.ZERO
                    : lineTotal.multiply(product.getTaxPercent()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            total = total.add(lineTotal).add(tax);
            reply.append(product.getName()).append(" - ").append(item.getQuantity().stripTrailingZeros().toPlainString())
                    .append(" ").append(product.getUnit()).append(" x ").append(product.getPrice().setScale(2, RoundingMode.HALF_UP));
            if (discount.signum() > 0) reply.append(" − discount ").append(discount);
            if (tax.signum() > 0) reply.append(" + ").append(product.getTaxType()).append(" ").append(tax);
            reply.append(" = ").append(lineTotal.add(tax)).append('\n');
        }
        conversation.setState(WhatsAppTestConversationState.AWAITING_CONFIRMATION);
        reply.append("Total: ").append(total.setScale(2, RoundingMode.HALF_UP)).append(' ')
                .append(businessRepository.getReferenceById(businessId).getCurrency())
                .append(". Reply YES to place the order, NO to cancel, or CANCEL.");
        return reply.toString();
    }

    private String removeCartItem(WhatsAppTestConversation conversation, String productIdText) {
        try {
            Long productId = Long.parseLong(productIdText);
            cartItemRepository.deleteByConversation_IdAndProduct_Id(conversation.getId(), productId);
            return "Removed product " + productId + " if it was in your cart. Reply with a product ID, REVIEW, or CANCEL.";
        } catch (NumberFormatException exception) {
            return "Use REMOVE followed by the product ID, for example REMOVE 12.";
        }
    }

    private boolean sameAmount(BigDecimal first, BigDecimal second) {
        return first == null ? second == null : second != null && first.compareTo(second) == 0;
    }

    private WhatsAppConversationResponse record(
            String messageId,
            WhatsAppTestConversation conversation,
            String reply
    ) {
        WhatsAppTestMessage message = new WhatsAppTestMessage();
        message.setBusiness(conversation.getBusiness());
        message.setConversation(conversation);
        message.setMessageId(messageId);
        message.setReplyText(reply);
        message.setState(conversation.getState());
        message.setOrderId(conversation.getOrderId());
        message.setOrderNumber(conversation.getOrderNumber());
        messageRepository.save(message);
        return new WhatsAppConversationResponse(conversation.getId(), conversation.getState(), reply,
                conversation.getOrderId(), conversation.getOrderNumber(), false);
    }

    private WhatsAppConversationResponse fromMessage(WhatsAppTestMessage message, boolean replayed) {
        return new WhatsAppConversationResponse(message.getConversation().getId(), message.getState(), message.getReplyText(),
                message.getOrderId(), message.getOrderNumber(), replayed);
    }
}
