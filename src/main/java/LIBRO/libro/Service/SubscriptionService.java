package LIBRO.libro.Service;


import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Payload.DTO.SubscriptionDto;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    PaymentInitiateResponse subscribe(SubscriptionDto subscriptionDTO) throws Exception;

    SubscriptionDto getUsersActiveSubscription(Long userId) throws SubscriptionException;

    SubscriptionDto cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException;

    SubscriptionDto activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException;

    List<SubscriptionDto> getAllSubscriptions(Pageable pageable);

    void deactivateExpiredSubscriptions() throws SubscriptionException;

}