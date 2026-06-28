package LIBRO.libro.Controllers;


import LIBRO.libro.Exceptions.SubscriptionPlanException;
import LIBRO.libro.Payload.DTO.SubscriptionplanDto;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<List<SubscriptionplanDto>> getAllSubscriptionPlans() {

        List<SubscriptionplanDto> plans = subscriptionPlanService.getAllSubscriptionsPlans();
        return ResponseEntity.ok(plans);

    }


    @PostMapping("/admin/create")
    public ResponseEntity<SubscriptionplanDto> createSubscriptionPlan(@Valid @RequestBody  SubscriptionplanDto subscriptionplanDto) throws SubscriptionPlanException {

        SubscriptionplanDto plan=subscriptionPlanService.createSubscription(subscriptionplanDto);

        return ResponseEntity.ok(plan);


    }

    @PutMapping("/admin/{id}/update")
    public ResponseEntity<SubscriptionplanDto> updatePlan( @PathVariable Long id , @RequestBody  SubscriptionplanDto subscriptionplanDto) throws SubscriptionPlanException {

        SubscriptionplanDto plan=subscriptionPlanService.updateSubscription(id, subscriptionplanDto);

        return ResponseEntity.ok(plan);



    }



    @DeleteMapping("/admin/{id}/delete")
    public ResponseEntity<ApiResponse> deleteplan(@PathVariable Long id) throws SubscriptionPlanException {

        subscriptionPlanService.deleteSubscription(id);

        ApiResponse apiResponse = new ApiResponse("plan deleted successfully" , true);


        return ResponseEntity.ok(apiResponse);


    }








}
