package LIBRO.libro.Payload.Response;

import LIBRO.libro.Domain.PaymentGateWay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateResponse {
    private Long paymentId;

    private PaymentGateWay gateway;

    private String transactionId;

    private String razorpayOrderId;

    private Long amount;

    private String description;

    private String checkoutUrl;

    private String message;

    private Boolean success;
}
