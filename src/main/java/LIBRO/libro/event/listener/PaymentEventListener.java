package LIBRO.libro.event.listener;


import LIBRO.libro.Entities.Payment;
import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class PaymentEventListener {
    private final SubscriptionService subscriptionService;
    private final LIBRO.libro.Service.FineService fineService;


    @EventListener
    @Transactional
    public void handlePaymentSuccess(Payment payment) throws SubscriptionException {
        switch(payment.getPaymentType()){
            case FINE :
            case LOST_BOOK_PENALTY:
            case DAMAGED_BOOK_PENALTY:
                if (payment.getFineId() != null) {
                    try {
                        fineService.markFineAsPaid(payment.getFineId(), payment.getAmount(), payment.getTransactionId());
                    } catch (Exception e) {
                        System.err.println("Failed to mark fine as paid: " + e.getMessage());
                    }
                }
                break;
            case MEMBERSHIP:
                subscriptionService.activateSubscription(payment.getSubscription().getId(),payment.getId());
                break;
        }
    }
}
