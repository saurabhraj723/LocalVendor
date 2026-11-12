package com.localflow.auth.service;

import com.localflow.auth.api.dto.ForgotPasswordRequest;
import com.localflow.auth.api.dto.ForgotPasswordResponse;
import com.localflow.auth.api.dto.ResetPasswordRequest;
import com.localflow.auth.domain.PasswordResetToken;
import com.localflow.auth.repository.PasswordResetTokenRepository;
import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.common.exception.UnauthorizedException;
import com.localflow.customer.domain.Customer;
import com.localflow.customer.repository.CustomerRepository;
import com.localflow.user.domain.User;
import com.localflow.user.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.HexFormat;

@Service
public class PasswordResetService {
    private static final String GENERIC_MESSAGE = "If an account matches these details, password reset instructions have been sent.";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository users;
    private final CustomerRepository customers;
    private final BusinessRepository businesses;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String frontendBaseUrl;
    private final String fromAddress;
    private final String mailHost;
    private final boolean devLinkEnabled;

    public PasswordResetService(UserRepository users, CustomerRepository customers, BusinessRepository businesses,
                                PasswordResetTokenRepository tokens, PasswordEncoder passwordEncoder,
                                ObjectProvider<JavaMailSender> mailSender,
                                @Value("${localflow.password-reset.frontend-base-url}") String frontendBaseUrl,
                                @Value("${localflow.password-reset.from:LocalFlow <no-reply@localhost>}") String fromAddress,
                                @Value("${spring.mail.host:}") String mailHost,
                                @Value("${localflow.password-reset.dev-link-enabled:false}") boolean devLinkEnabled) {
        this.users = users;
        this.customers = customers;
        this.businesses = businesses;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.frontendBaseUrl = frontendBaseUrl;
        this.fromAddress = fromAddress;
        this.mailHost = mailHost;
        this.devLinkEnabled = devLinkEnabled;
    }

    @Transactional
    public ForgotPasswordResponse requestVendorReset(ForgotPasswordRequest request) {
        tokens.deleteAllByExpiresAtBefore(OffsetDateTime.now());
        return users.findByEmail(normalize(request.email()))
                .map(user -> issue(user, null, user.getEmail(), user.getName(), null))
                .orElseGet(this::genericResponse);
    }

    @Transactional
    public ForgotPasswordResponse requestCustomerReset(ForgotPasswordRequest request) {
        tokens.deleteAllByExpiresAtBefore(OffsetDateTime.now());
        if (request.storefrontSlug() == null || request.storefrontSlug().isBlank()) {
            return genericResponse();
        }
        Business business = businesses.findByStorefrontSlug(request.storefrontSlug().trim().toLowerCase()).orElse(null);
        if (business == null) return genericResponse();
        return customers.findByBusiness_IdAndEmailIgnoreCase(business.getId(), normalize(request.email()))
                .filter(customer -> customer.getPasswordHash() != null)
                .map(customer -> issue(null, customer, customer.getEmail(), customer.getName(), business.getStorefrontSlug()))
                .orElseGet(this::genericResponse);
    }

    @Transactional
    public void resetVendorPassword(ResetPasswordRequest request) {
        PasswordResetToken token = validToken(request.token(), true);
        User user = users.findById(token.getUserId()).orElseThrow(this::invalidToken);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        token.setUsedAt(OffsetDateTime.now());
        tokens.deleteAllByUserIdAndIdNot(user.getId(), token.getId());
    }

    @Transactional
    public void resetCustomerPassword(ResetPasswordRequest request) {
        PasswordResetToken token = validToken(request.token(), false);
        Customer customer = customers.findById(token.getCustomerId()).orElseThrow(this::invalidToken);
        customer.setPasswordHash(passwordEncoder.encode(request.password()));
        token.setUsedAt(OffsetDateTime.now());
        tokens.deleteAllByCustomerIdAndIdNot(customer.getId(), token.getId());
    }

    private ForgotPasswordResponse issue(User user, Customer customer, String email, String name, String storefrontSlug) {
        String rawToken = newToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user == null ? null : user.getId());
        token.setCustomerId(customer == null ? null : customer.getId());
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(OffsetDateTime.now().plusMinutes(30));
        tokens.save(token);

        String link = frontendBaseUrl.replaceAll("/$", "") + "/?resetToken=" + rawToken;
        if (storefrontSlug != null) link += "&shop=" + URLEncoder.encode(storefrontSlug, StandardCharsets.UTF_8);
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender != null && !mailHost.isBlank()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(email);
            message.setSubject("Reset your LocalFlow password");
            message.setText("Hello " + name + ",\n\nUse this link within 30 minutes to reset your password:\n" + link + "\n\nIf you did not request this, you can ignore this email.");
            try { sender.send(message); }
            catch (RuntimeException exception) {
                LOGGER.warn("Password reset email delivery failed ({}): {}",
                        exception.getClass().getSimpleName(), exception.getMessage(), exception);
            }
        }
        return new ForgotPasswordResponse(GENERIC_MESSAGE, devLinkEnabled ? link : null, deliveryMode());
    }

    private ForgotPasswordResponse genericResponse() {
        return new ForgotPasswordResponse(GENERIC_MESSAGE, null, deliveryMode());
    }

    private String deliveryMode() {
        if (devLinkEnabled) return "DEVELOPMENT_LINK";
        return !mailHost.isBlank() && mailSender.getIfAvailable() != null ? "EMAIL" : "NOT_CONFIGURED";
    }

    private PasswordResetToken validToken(String rawToken, boolean vendor) {
        PasswordResetToken token = tokens.findByTokenHashForUpdate(hash(rawToken)).orElseThrow(this::invalidToken);
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(OffsetDateTime.now())
                || (vendor ? token.getUserId() == null : token.getCustomerId() == null)) {
            throw invalidToken();
        }
        return token;
    }

    private UnauthorizedException invalidToken() {
        return new UnauthorizedException("INVALID_PASSWORD_RESET_TOKEN", "This password reset link is invalid or has expired.");
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String hash(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException("SHA-256 is unavailable", e); }
    }

    private static String normalize(String email) { return email.trim().toLowerCase(); }
}
