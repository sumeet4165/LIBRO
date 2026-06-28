package LIBRO.libro.Mapper;


import LIBRO.libro.Entities.Payment;
import LIBRO.libro.Payload.DTO.PaymentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDTO(Payment payment) {

        if (payment == null) {
            return null;
        }

        PaymentDto dto = new PaymentDto();

        dto.setId(payment.getId());

        // User information
        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getId());
            dto.setUserName(payment.getUser().getFullName());
            dto.setUserEmail(payment.getUser().getEmail());
        }

//        // Book loan information
//        if (payment.() != null) {
//            dto.setBookLoanId(payment.getBookLoan().getId());
//        }

        // Subscription information
        if (payment.getSubscription() != null) {
            dto.setSubscriptionId(payment.getSubscription().getId());
        }

        dto.setPaymentType(payment.getPaymentType());
        dto.setStatus(payment.getStatus());
        dto.setGateway(payment.getPaymentGateWay());
        dto.setAmount(payment.getAmount());

        dto.setTransactionId(payment.getTransactionId());
        dto.setGatewayPaymentId(payment.getGatewayPaymentId());
        dto.setGatewayOrderId(payment.getGatewayOrderId());
        dto.setGatewaySignature(payment.getGatewaySignature());


        dto.setDescription(payment.getDescription());
        dto.setFailureReason(payment.getFailureReason());


        dto.setInitiatedAt(payment.getInitiatedAt());
        dto.setCompletedAt(payment.getCompletedAt());

        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;
    }
}