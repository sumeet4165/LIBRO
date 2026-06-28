package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.PaymentGateWay;
import LIBRO.libro.Domain.PaymentStatus;
import LIBRO.libro.Entities.Payment;
import LIBRO.libro.Entities.Subscription;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Gateway.RazorpayService;
import LIBRO.libro.Mapper.PaymentMapper;
import LIBRO.libro.Payload.DTO.PaymentDto;
import LIBRO.libro.Payload.Request.PaymentInitiateRequest;
import LIBRO.libro.Payload.Request.PaymentVerifyRequest;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;
import LIBRO.libro.Payload.Response.PaymentLinkResponse;
import LIBRO.libro.Repositeries.PaymentRepo;
import LIBRO.libro.Repositeries.SubscriptionRepo;
import LIBRO.libro.Repositeries.UserRepo;
import LIBRO.libro.Service.PaymentService;
import LIBRO.libro.event.publisher.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentServiceimpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final RazorpayService razorpayService;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws Exception {
        User user =userRepo.findById(request.getUserId()).get();

        Payment payment = new Payment();

        payment.setUser(user);
        payment.setPaymentType(request.getPaymentType());
        payment.setPaymentGateWay(request.getGateway());
        payment.setAmount(request.getAmount());

        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());
        payment.setInitiatedAt(LocalDateTime.now());

        if (request.getSubscriptionId() != null) {
            Subscription sub = subscriptionRepo.findById(request.getSubscriptionId())
                    .orElseThrow(() -> new Exception("Subscription not found"));

            payment.setSubscription(sub);
        }

        if (request.getFineId() != null) {
            payment.setFineId(request.getFineId());
        }

        payment = paymentRepo.save(payment);


        PaymentInitiateResponse response = new PaymentInitiateResponse();

        if(request.getGateway() == PaymentGateWay.RAZORPAY){
            PaymentLinkResponse paymentLinkResponse = razorpayService.createPayementLink(user, payment);
            response =PaymentInitiateResponse.builder()
                    .paymentId(payment.getId())
                    .gateway(payment.getPaymentGateWay())
                    .checkoutUrl(paymentLinkResponse.getPayment_link_url())
                    .transactionId(paymentLinkResponse.getPayment_link_id())
                    .amount(payment.getAmount())
                    .description(payment.getDescription())
                    .success(true)
                    .message("Payment initiated successfully")
                    .build();
            payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());


        }
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepo.save(payment);

//        payment initiate event


        return response;



    }

    @Override
    public PaymentDto verifyPayment(PaymentVerifyRequest request) throws Exception {

        JSONObject paymentDetails= razorpayService.fetchPaymentDetails(request.getRazorpayPaymentId());
        JSONObject notes=paymentDetails.getJSONObject("notes");

        Long paymentId=Long.parseLong(notes.optString("payment_id"));
        Payment payment =paymentRepo.findById(paymentId).get();

        boolean isvalid= razorpayService.isValidPayment(request.getRazorpayPaymentId());

        if(PaymentGateWay.RAZORPAY==payment.getPaymentGateWay()){
            if(isvalid){
                payment.setStatus(PaymentStatus.PENDING);
                payment.setGatewayOrderId(request.getRazorpayPaymentId());
            }
        }

        if(isvalid){
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payment=paymentRepo.save(payment);

//            publish payment succes event todo

            paymentEventPublisher.publishPaymentSuccesEvent(payment);

        }

        return paymentMapper.toDTO(payment);





    }

    @Override
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepo.findAll(pageable);
        return payments.map(paymentMapper::toDTO);


    }
}
