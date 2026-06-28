package LIBRO.libro.Service;

import LIBRO.libro.Entities.SubscriptionPlan;
import LIBRO.libro.Exceptions.SubscriptionPlanException;
import LIBRO.libro.Payload.DTO.SubscriptionplanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SubscriptionPlanService {

    SubscriptionplanDto createSubscription(SubscriptionplanDto subs) throws SubscriptionPlanException;
    SubscriptionplanDto updateSubscription(Long id ,SubscriptionplanDto subs) throws SubscriptionPlanException;

    void deleteSubscription(Long id) throws SubscriptionPlanException;

    List<SubscriptionplanDto> getAllSubscriptionsPlans() ;

    SubscriptionplanDto getSubscriptionById(Long id) throws SubscriptionPlanException;

        SubscriptionPlan getBySubscriptionplancode(String subscriptionplancode) throws SubscriptionPlanException;

    }
