package com.localflow.auth.api;

import com.localflow.auth.api.dto.CustomerAuthResponse;
import com.localflow.auth.api.dto.CustomerLoginRequest;
import com.localflow.auth.api.dto.CustomerRegisterRequest;
import com.localflow.auth.api.dto.ForgotPasswordRequest;
import com.localflow.auth.api.dto.ForgotPasswordResponse;
import com.localflow.auth.api.dto.ResetPasswordRequest;
import com.localflow.auth.service.CustomerAuthService;
import com.localflow.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer-auth")
public class CustomerAuthController {
    private final CustomerAuthService customerAuthService;
    private final PasswordResetService passwordResetService;

    public CustomerAuthController(CustomerAuthService customerAuthService, PasswordResetService passwordResetService) {
        this.customerAuthService = customerAuthService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAuthResponse register(@Valid @RequestBody CustomerRegisterRequest request) {
        return customerAuthService.register(request);
    }

    @PostMapping("/login")
    public CustomerAuthResponse login(@Valid @RequestBody CustomerLoginRequest request) {
        return customerAuthService.login(request);
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return passwordResetService.requestCustomerReset(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetCustomerPassword(request);
    }
}
