package LIBRO.libro.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn( nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn( nullable = false)
    private SubscriptionPlan subscriptionPlan;

    private String planName;
    private String planCode;
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Long price;

    private String currency = "INR";

    @Column(nullable = false)
    private Integer maxBooksAllowed;

    @Column(nullable = false)
    private Integer maxDaysPerBook;

    private Boolean autoRenew;

    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private String notes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isValid(){
        if(!active){
            return false;
        }

        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isExpired(){
        return LocalDate.now().isAfter(endDate);
    }

    public long getDaysRemaining(){
        if(isExpired()){
            return 0;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public void calculateEndDate(){
        if(startDate != null && subscriptionPlan != null){
            this.endDate = startDate.plusDays(subscriptionPlan.getDurationDays());
        }
    }

    public void initializeFromPlan(){
        if(subscriptionPlan != null){

            this.planName = subscriptionPlan.getName();
            this.planCode = subscriptionPlan.getPlanCode();
            this.price = subscriptionPlan.getPrice();
            this.maxBooksAllowed = subscriptionPlan.getMaxbooksAlllowed();
            this.maxDaysPerBook = subscriptionPlan.getMaxDaysPerBook();
            this.durationDays = subscriptionPlan.getDurationDays();

            if(startDate == null){
                this.startDate = LocalDate.now();
            }

            calculateEndDate();
        }
    }
}


