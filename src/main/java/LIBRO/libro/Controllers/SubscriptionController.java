package LIBRO.libro.Controllers;


import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Payload.DTO.SubscriptionDto;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;
import LIBRO.libro.Service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;



        @PostMapping("/subscribe")
        public ResponseEntity<?> subscribe(
              @RequestBody SubscriptionDto subscription
        ) throws Exception {
            PaymentInitiateResponse res = subscriptionService.subscribe(subscription);
            return ResponseEntity.ok(res);
        }

    @GetMapping("/user/active")
    public ResponseEntity<?> getUsersActiveSubscriptions(@RequestParam(required = false) Long userId) throws SubscriptionException {



        SubscriptionDto subscription = subscriptionService.getUsersActiveSubscription(userId);

        return ResponseEntity.ok(subscription);
    }



        @GetMapping("/admin")
        public ResponseEntity<?> getAllSubscriptions() {
            int page = 0;
            int size = 10;

            Pageable pageable = PageRequest.of(page, size);

            List<SubscriptionDto> dtoList =
                    subscriptionService.getAllSubscriptions(pageable);

            return ResponseEntity.ok(dtoList);
        }

        @GetMapping("/admin/deactivate-expired")
        public ResponseEntity<?> deactivateExpiredSubscriptions() throws SubscriptionException {
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            subscriptionService.deactivateExpiredSubscriptions();

            ApiResponse apiResponse = new ApiResponse("task done ! " , true);
            return ResponseEntity.ok(apiResponse);

    }


    @PostMapping("/cancel/{subscriptionId}")
    public ResponseEntity<?> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam(required = false) String reason
    ) throws SubscriptionException {

        SubscriptionDto subscription = subscriptionService.cancelSubscription(subscriptionId, reason);

        return ResponseEntity.ok(subscription);
    }


    @PostMapping("/activate")
    public ResponseEntity<?> activateSubscription(
            @RequestParam Long subscriptionId,
            @RequestParam Long paymentId) throws SubscriptionException {

        SubscriptionDto subscription =
                subscriptionService.activateSubscription(subscriptionId, paymentId);

        return ResponseEntity.ok(subscription);
    }





}
