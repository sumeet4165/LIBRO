package LIBRO.libro.event.publisher;


import LIBRO.libro.Entities.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;


    public void publishPaymentSuccesEvent(Payment payment){
        applicationEventPublisher.publishEvent(payment);

    }
}
