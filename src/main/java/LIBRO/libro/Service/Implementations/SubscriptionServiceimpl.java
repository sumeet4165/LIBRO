package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.PaymentGateWay;
import LIBRO.libro.Domain.PaymentType;
import LIBRO.libro.Entities.Subscription;
import LIBRO.libro.Entities.SubscriptionPlan;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Mapper.SubscriptionMapper;
import LIBRO.libro.Mapper.UserMapper;
import LIBRO.libro.Payload.DTO.SubscriptionDto;
import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Payload.Request.PaymentInitiateRequest;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;
import LIBRO.libro.Repositeries.SubscriptionRepo;
import LIBRO.libro.Repositeries.SubscroiptionPlanrepo;
import LIBRO.libro.Service.PaymentService;
import LIBRO.libro.Service.SubscriptionService;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SubscriptionServiceimpl implements SubscriptionService {

    private final UserService userService;
    private final SubscriptionRepo subscriptionRepo;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscroiptionPlanrepo subscroiptionPlanrepo;
    private final UserMapper userMapper;
    private final PaymentService paymentService;

    @Override
    public PaymentInitiateResponse subscribe(SubscriptionDto subscriptionDTO) throws Exception {
        User user=userService.getCurrentUser();


        SubscriptionPlan plan = subscroiptionPlanrepo
                .findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new Exception("Plan not found!"));

        // Optional<Sub>

        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO,plan,user);

        subscription.initializeFromPlan();
        subscription.setActive(false);

        Subscription savedSubscription = subscriptionRepo.save(subscription);

        // create payment (todo)

        PaymentInitiateRequest  paymentInitiateRequest = PaymentInitiateRequest.builder()
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .paymentType(PaymentType.MEMBERSHIP)
                .gateway(PaymentGateWay.RAZORPAY)
                .amount(subscription.getPrice())
                .description("Library Subscription - " +plan.getName())
                .build();



        return paymentService.initiatePayment(paymentInitiateRequest);



    }



    @Override
    public SubscriptionDto getUsersActiveSubscription(Long userId) throws SubscriptionException {

        User user=userService.getCurrentUser();

        Subscription subscription = subscriptionRepo
                .findActiveSubscriptionByUserId(user.getId(), LocalDate.now())
                .orElseThrow(() -> new SubscriptionException("no active subscription found!"));

        return subscriptionMapper.toDto(subscription);
    }



    @Override
    public SubscriptionDto cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException {
        // 1. Find subscription
        Subscription subscription = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException(
                        "Subscription not found with ID: " + subscriptionId));

        if (!subscription.getActive()) {
            throw new SubscriptionException("Subscription is already inactive");
        }

        subscription.setActive(false);
        subscription.setCancellationDate(LocalDateTime.now());
        subscription.setCancellationReason(
                reason != null ? reason : "Cancelled by user"
        );

        subscription = subscriptionRepo.save(subscription);



        // 6. Convert Entity → DTO and return
        return subscriptionMapper.toDto(subscription);
    }


    @Override
    public SubscriptionDto activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException {

        Subscription subscription = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() ->
                        new SubscriptionException("Subscription not found by id!"));

//        // 2. Verify payment (logic can vary depending on system)
//        if (paymentId == null) {
//            throw new SubscriptionException("Payment verification failed!");
//        }

        // 3. Activate subscription
        subscription.setActive(true);

        // 4. Save subscription
        subscription = subscriptionRepo.save(subscription);

        // 5. Convert Entity → DTO
        return subscriptionMapper.toDto(subscription);
    }


    @Override
    public List<SubscriptionDto> getAllSubscriptions(Pageable pageable) {
        List<Subscription> subscriptions = subscriptionRepo.findAll();
        return subscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    public void deactivateExpiredSubscriptions() throws SubscriptionException {

        List<Subscription> expiredSubscriptions =
                subscriptionRepo.findExpiredActiveSubscriptions(LocalDate.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setActive(false);
            subscriptionRepo.save(subscription);
        }
    }


}
