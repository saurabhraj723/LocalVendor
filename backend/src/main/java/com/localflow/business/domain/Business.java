package com.localflow.business.domain;

import com.localflow.common.domain.BusinessStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "business_type", nullable = false, length = 80)
    private String businessType;

    @Column(name = "storefront_slug", nullable = false, unique = true, length = 48)
    private String storefrontSlug;

    @Column(name = "customer_registration_code", nullable = false, unique = true, length = 32)
    private String customerRegistrationCode;

    @Column(length = 32)
    private String phone;

    @Column(length = 15)
    private String gstin;

    @Column(length = 254)
    private String email;

    @Column(columnDefinition = "text")
    private String address;

    @Column(nullable = false, length = 3)
    private String currency = "INR";

    @Column(nullable = false, length = 80)
    private String timezone = "Asia/Kolkata";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private BusinessStatus status = BusinessStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getStorefrontSlug() { return storefrontSlug; }
    public void setStorefrontSlug(String storefrontSlug) { this.storefrontSlug = storefrontSlug; }
    public String getCustomerRegistrationCode() { return customerRegistrationCode; }
    public void setCustomerRegistrationCode(String customerRegistrationCode) { this.customerRegistrationCode = customerRegistrationCode; }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getPhone() {
        return phone;
    }

    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public BusinessStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}

