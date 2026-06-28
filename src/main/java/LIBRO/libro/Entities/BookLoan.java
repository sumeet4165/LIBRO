package LIBRO.libro.Entities;

import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Domain.BookLoanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;


    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    private BookLoanType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 20)
    private BookLoanStatus status;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalDate dueDate;


    private LocalDate returnDate;


    @Column(nullable = false)
    private Integer renewalCount=0;

    @Column(nullable = false)
    private Integer maxRenewals=2;

//    fine todo

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private boolean isOverdue=false;

    @Column(nullable = false)
    private Integer overdueDays=0;



    @CreationTimestamp
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isActive(){
        return status==BookLoanStatus.CHECKED_OUT || status==BookLoanStatus.OVERDUE;
    }


    public boolean canRenew() {
        return status==BookLoanStatus.CHECKED_OUT && !isOverdue && renewalCount<maxRenewals;
    }
}
