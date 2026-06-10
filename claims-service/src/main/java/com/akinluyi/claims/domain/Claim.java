package com.akinluyi.claims.domain;

import com.akinluyi.claims.common.domain.ClaimStatus;
import com.akinluyi.claims.common.domain.ClaimType;
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
import java.math.BigDecimal;
import java.time.Instant;

/** Persistent insurance-claim aggregate. */
@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String claimNumber;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private String claimantName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType type;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    private String rejectionReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Claim() {
        // for JPA
    }

    public Claim(String claimNumber, String policyNumber, String claimantName,
                 ClaimType type, String description, BigDecimal amount) {
        this.claimNumber = claimNumber;
        this.policyNumber = policyNumber;
        this.claimantName = claimantName;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.status = ClaimStatus.SUBMITTED;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void approve() {
        this.status = ClaimStatus.APPROVED;
    }

    public void reject(String reason) {
        this.status = ClaimStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public Long getId() {
        return id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public ClaimType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
