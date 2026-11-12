package com.localflow.catalogue.service;

import com.localflow.business.repository.BusinessRepository;
import com.localflow.catalogue.api.dto.AvailabilityRequest;
import com.localflow.catalogue.api.dto.ProductRequest;
import com.localflow.catalogue.api.dto.ProductResponse;
import com.localflow.catalogue.domain.Category;
import com.localflow.catalogue.domain.Product;
import com.localflow.catalogue.repository.CategoryRepository;
import com.localflow.catalogue.repository.ProductRepository;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final CurrentUserProvider currentUserProvider;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            BusinessRepository businessRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        Long businessId = currentUserProvider.currentBusinessId();
        return productRepository.findAllByBusiness_IdAndActiveTrueOrderByNameAsc(businessId)
                .stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Long businessId = currentUserProvider.currentBusinessId();
        return ProductResponse.from(findActiveProduct(businessId, id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        String sku = normalizeSku(request.sku());
        ensureSkuAvailable(businessId, sku, null);

        Product product = new Product();
        product.setBusiness(businessRepository.getReferenceById(businessId));
        product.setAvailable(true);
        apply(product, request, sku, businessId);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        Product product = findActiveProduct(businessId, id);
        String sku = normalizeSku(request.sku());
        ensureSkuAvailable(businessId, sku, id);
        apply(product, request, sku, businessId);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse setAvailability(Long id, AvailabilityRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        Product product = findActiveProduct(businessId, id);
        product.setAvailable(request.available());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void archive(Long id) {
        Long businessId = currentUserProvider.currentBusinessId();
        Product product = findActiveProduct(businessId, id);
        product.setActive(false);
        product.setAvailable(false);
        productRepository.save(product);
    }

    private Product findActiveProduct(Long businessId, Long id) {
        return productRepository.findByIdAndBusiness_IdAndActiveTrue(id, businessId)
                .orElseThrow(() -> new NotFoundException("PRODUCT_NOT_FOUND", "Product was not found."));
    }

    private void ensureSkuAvailable(Long businessId, String sku, Long excludeId) {
        if (sku == null) {
            return;
        }
        boolean exists = excludeId == null
                ? productRepository.existsByBusiness_IdAndSkuIgnoreCase(businessId, sku)
                : productRepository.existsByBusiness_IdAndSkuIgnoreCaseAndIdNot(businessId, sku, excludeId);
        if (exists) {
            throw new ConflictException("PRODUCT_SKU_ALREADY_EXISTS", "A product with this SKU already exists.");
        }
    }

    private void apply(Product product, ProductRequest request, String sku, Long businessId) {
        product.setName(request.name().trim());
        product.setDescription(request.description() == null || request.description().isBlank()
                ? null : request.description().trim());
        product.setSku(sku);
        product.setUnit(request.unit().trim());
        product.setPrice(request.price());
        product.setDiscountPercent(request.discountPercent() == null ? BigDecimal.ZERO : request.discountPercent());
        product.setTaxType(request.taxType() == null || request.taxType().isBlank() ? null : request.taxType());
        product.setTaxPercent(product.getTaxType() == null ? null : request.taxPercent());
        product.setMinQuantity(request.minQuantity() == null ? BigDecimal.ONE : request.minQuantity());
        product.setQuantityStep(request.quantityStep() == null ? BigDecimal.ONE : request.quantityStep());
        product.setCategory(resolveCategory(request.categoryId(), businessId));
    }

    private Category resolveCategory(Long categoryId, Long businessId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndBusiness_Id(categoryId, businessId)
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND", "Category was not found."));
    }

    private static String normalizeSku(String sku) {
        return sku == null || sku.isBlank() ? null : sku.trim();
    }
}
