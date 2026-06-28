package LIBRO.libro.Mapper;

import LIBRO.libro.Entities.Subscription;
import LIBRO.libro.Entities.SubscriptionPlan;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Payload.DTO.SubscriptionDto;
import LIBRO.libro.Repositeries.SubscroiptionPlanrepo;
import LIBRO.libro.Repositeries.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final UserRepo userRepo;
    private final SubscroiptionPlanrepo subscroiptionPlanrepo;

    public  SubscriptionDto toDto(Subscription subscription)  {

        if (subscription == null) {
            return null;
        }

        SubscriptionDto dto = new SubscriptionDto();

        dto.setId(subscription.getId());

        if(subscription.getUser()!=null){
            dto.setUserId(subscription.getUser().getId());
            dto.setUserName(subscription.getUser().getFullName());
            dto.setUserEmail(subscription.getUser().getEmail());
        }

        if(subscription.getSubscriptionPlan()!=null){
            dto.setPlanId(subscription.getSubscriptionPlan().getId());
        }

        dto.setPlanName(subscription.getPlanName());
        dto.setPlanCode(subscription.getPlanCode());

        dto.setPrice(subscription.getPrice());
        dto.setCurrency(subscription.getCurrency());


        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());

        dto.setIsActive(subscription.getActive());
        dto.setMaxBooksAllowed(subscription.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(subscription.getMaxDaysPerBook());

        dto.setAutoRenew(subscription.getAutoRenew());

        dto.setCancelledAt(subscription.getCancellationDate());
        dto.setCancellationReason(subscription.getCancellationReason());

        dto.setNotes(subscription.getNotes());

        dto.setDaysRemaining(subscription.getDaysRemaining());
        dto.setIsValid(subscription.isValid());
        dto.setIsExpired(subscription.isExpired());

        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());

        return dto;
    }


    public  Subscription toEntity(SubscriptionDto dto , SubscriptionPlan subscriptionPlan, User user ) throws SubscriptionException {

        if (dto == null) {
            return null;
        }

        Subscription subscription = new Subscription();

        subscription.setId(dto.getId());
        subscription.setUser(user);
        subscription.setSubscriptionPlan(subscriptionPlan);


//
//        subscription.setPlanName(dto.getPlanName());
//        subscription.setPlanCode(dto.getPlanCode());
//        subscription.setPrice(dto.getPrice());
//        subscription.setCurrency(dto.getCurrency());
//
//        subscription.setStartDate(dto.getStartDate());
//        subscription.setEndDate(dto.getEndDate());
//
//        subscription.setActive(dto.getIsActive());
//        subscription.setMaxBooksAllowed(dto.getMaxBooksAllowed());
//        subscription.setMaxDaysPerBook(dto.getMaxDaysPerBook());
//
//        subscription.setAutoRenew(dto.getAutoRenew());
//
//        subscription.setCancellationDate(dto.getCancelledAt());
//        subscription.setCancellationReason(dto.getCancellationReason());

        subscription.setNotes(dto.getNotes());

        return subscription;
    }


    /**
     * Convert list of subscriptions to DTOs
     */
    public List<SubscriptionDto> toDTOList(List<Subscription> subscriptions) {

        if (subscriptions == null) {
            return null;
        }

        return subscriptions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }




}