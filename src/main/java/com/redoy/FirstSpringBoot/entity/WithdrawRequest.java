package com.redoy.FirstSpringBoot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdraw_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long withdrawId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private UserAccount teacher;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    private String paymentDetails; // bKash/Bank details
    
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
}
