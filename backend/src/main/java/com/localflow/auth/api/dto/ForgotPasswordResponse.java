package com.localflow.auth.api.dto;

public record ForgotPasswordResponse(String message, String developmentResetUrl, String deliveryMode) { }
