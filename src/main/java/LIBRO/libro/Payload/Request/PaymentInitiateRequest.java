package LIBRO.libro.Payload.Request;

import LIBRO.libro.Domain.PaymentGateWay;
import LIBRO.libro.Domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private Long bookLoanId; // Required only for FINE payments

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateWay gateway; // RAZORPAY or STRIPE

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Long fineId;

    private Long subscriptionId;

    // Return URLs for payment gateway redirects
    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    private String successUrl;

    @Size(max = 500, message = "Cancel URL must not exceed 500 characters")
    private String cancelUrl;
}

