package com.localflow.auth.service;

import com.localflow.auth.api.dto.AuthResponse;
import com.localflow.auth.api.dto.LoginRequest;
import com.localflow.auth.api.dto.RegisterOwnerRequest;
import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.common.domain.UserRole;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.UnauthorizedException;
import com.localflow.user.domain.User;
import com.localflow.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            BusinessRepository businessRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public AuthResponse registerOwner(RegisterOwnerRequest request) {
        String email = normalizeEmail(request.ownerEmail());
        if (userRepository.existsByEmail(email) || businessRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("EMAIL_ALREADY_REGISTERED", "An account or business profile with this email already exists.");
        }

        Business business = new Business();
        business.setName(request.businessName().trim());
        business.setBusinessType(request.businessType().trim());
        business.setGstin(blankToNull(request.gstin()) == null ? null : request.gstin().trim().toUpperCase());
        business.setStorefrontSlug(randomCode(32).toLowerCase());
        business.setCustomerRegistrationCode(randomCode(32));
        business.setPhone(blankToNull(request.businessPhone()));
        business.setEmail(blankToNull(request.businessEmail()));
        business.setAddress(blankToNull(request.businessAddress()));
        business.setCurrency(defaultValue(request.currency(), "INR").toUpperCase());
        business.setTimezone(defaultValue(request.timezone(), "Asia/Kolkata"));
        business = businessRepository.save(business);

        User user = new User();
        user.setBusiness(business);
        user.setName(request.ownerName().trim());
        user.setEmail(email);
        user.setPhone(blankToNull(request.ownerPhone()));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.BUSINESS_OWNER);
        user = userRepository.save(user);

        return toAuthResponse(user, business);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", "Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Invalid email or password.");
        }

        return toAuthResponse(user, user.getBusiness());
    }

    private AuthResponse toAuthResponse(User user, Business business) {
        JwtTokenService.Token token = jwtTokenService.createAccessToken(user);
        return new AuthResponse(
                token.value(),
                "Bearer",
                token.expiresInSeconds(),
                new AuthResponse.UserSummary(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole()),
                new AuthResponse.BusinessSummary(business.getId(), business.getName(), business.getBusinessType(), business.getCurrency(), business.getTimezone(), business.getStorefrontSlug(), business.getCustomerRegistrationCode(), business.getGstin(), business.getPhone(), business.getEmail(), business.getAddress())
        );
    }

    private static String randomCode(int length) {
        String value = java.util.UUID.randomUUID().toString().replace("-", "")
                + java.util.UUID.randomUUID().toString().replace("-", "");
        return value.substring(0, length);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String defaultValue(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}

