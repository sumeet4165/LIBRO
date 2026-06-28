package LIBRO.libro.Entities;

import LIBRO.libro.Domain.FineStatus;
import LIBRO.libro.Domain.FineType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BookLoan bookLoan;

    private FineType type;



    @Column(nullable = false)
    private Long amount;

    private FineStatus status;

    @Column(length =500 )
    private String reason;

    @Column(length =500 )
    private String notes;


    @ManyToOne
    private User waivedBy;


    @Column(name = "waived_at")
    private LocalDateTime waivedAt;

    @Column(name = "waiver_reason", length = 500)
    private String waiverReason;

    // Payment tracking
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void applyPayment(Long paymentamount) {
        if (paymentamount == null || paymentamount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Update status based on amount paid
        this.status = FineStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void waive(User admin,  String reason) {
        this.status = FineStatus.WAIVED;
        this.waivedBy = admin;
        this.waivedAt = LocalDateTime.now();
        this.waiverReason = reason;
    }
}









