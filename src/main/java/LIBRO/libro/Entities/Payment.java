package LIBRO.libro.Entities;


import LIBRO.libro.Domain.PaymentGateWay;
import LIBRO.libro.Domain.PaymentStatus;
import LIBRO.libro.Domain.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subscription subscription;

    private Long fineId;

    private PaymentType paymentType;
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentGateWay paymentGateWay;


    private Long amount;

    private String transactionId;

    private String gatewayPaymentId;

    private String gatewayOrderId;


    private String gatewaySignature;

    private String description;

    private String failureReason;

    @CreationTimestamp
    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}










