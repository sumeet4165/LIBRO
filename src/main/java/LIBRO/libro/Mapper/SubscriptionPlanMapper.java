package LIBRO.libro.Mapper;

import LIBRO.libro.Entities.SubscriptionPlan;
import LIBRO.libro.Payload.DTO.SubscriptionplanDto;
import org.springframework.stereotype.Component;

@Component

public class SubscriptionPlanMapper {

    public SubscriptionplanDto toDto(SubscriptionPlan subscriptionPlan) {
        if(subscriptionPlan == null) return null;

        SubscriptionplanDto subscriptionplanDto = SubscriptionplanDto.builder()
                .id(subscriptionPlan.getId())
                .name(subscriptionPlan.getName())
                .description(subscriptionPlan.getDescription())
                .planCode(subscriptionPlan.getPlanCode())
                .durationDays(subscriptionPlan.getDurationDays())
                .price(subscriptionPlan.getPrice())
                .currency(subscriptionPlan.getCurrency())
                .maxBooksAllowed(subscriptionPlan.getMaxbooksAlllowed())
                .maxDaysPerBook(subscriptionPlan.getMaxDaysPerBook())
                .displayOrder(subscriptionPlan.getDisplayOrder())
                .isActive(subscriptionPlan.getIsActive())
                .isFeatured(subscriptionPlan.getIsFeatured())
                .badgeText(subscriptionPlan.getBadgeText())
                .createdAt(subscriptionPlan.getCreatedAt())
                .updatedAt(subscriptionPlan.getUpdatedAt())
                .createdBy(subscriptionPlan.getCreatedBy())
                .updatedBy(subscriptionPlan.getUpdatedBy())
                .adminNotes(subscriptionPlan.getAdminNotes())


                .build();

        return subscriptionplanDto;
    }

    public SubscriptionPlan toEntity(SubscriptionplanDto subscriptionplanDto) {
        if(subscriptionplanDto == null) return null;
        SubscriptionPlan subscriptionplan =SubscriptionPlan
                .builder()
                .id(subscriptionplanDto.getId())
                .name(subscriptionplanDto.getName())
                .description(subscriptionplanDto.getDescription())
                .planCode(subscriptionplanDto.getPlanCode())
                .durationDays(subscriptionplanDto.getDurationDays())
                .price(subscriptionplanDto.getPrice())
                .currency(subscriptionplanDto.getCurrency())
                .maxbooksAlllowed(subscriptionplanDto.getMaxBooksAllowed())
                .maxDaysPerBook(subscriptionplanDto.getMaxDaysPerBook())
                .displayOrder(subscriptionplanDto.getDisplayOrder())
                .isActive(subscriptionplanDto.getIsActive())
                .isFeatured(subscriptionplanDto.getIsFeatured())
                .badgeText(subscriptionplanDto.getBadgeText())
                .adminNotes(subscriptionplanDto.getAdminNotes())
                .CreatedBy(subscriptionplanDto.getCreatedBy())
                .UpdatedBy(subscriptionplanDto.getUpdatedBy())


                .build();

        return subscriptionplan;
    }


    public void update(SubscriptionPlan subscriptionPlan, SubscriptionplanDto subscriptionplanDto) {

        if(subscriptionplanDto == null || subscriptionPlan==null) return;

//                dont update id or plancode

        if (subscriptionplanDto.getName() != null) {
            subscriptionPlan.setName(subscriptionplanDto.getName());
        }
        if (subscriptionplanDto.getDescription() != null) {
            subscriptionPlan.setDescription(subscriptionplanDto.getDescription());
        }
        if(subscriptionplanDto.getDurationDays()!=null){
            subscriptionPlan.setDurationDays(subscriptionplanDto.getDurationDays());
        }
        if(subscriptionplanDto.getPrice()!=null){
            subscriptionPlan.setPrice(subscriptionplanDto.getPrice());
        }
        if(subscriptionplanDto.getCurrency()!=null){
            subscriptionPlan.setCurrency(subscriptionplanDto.getCurrency());
        }
        if(subscriptionplanDto.getMaxBooksAllowed()!=null){
            subscriptionPlan.setMaxbooksAlllowed(subscriptionplanDto.getMaxBooksAllowed());
        }
        if(subscriptionplanDto.getMaxDaysPerBook()!=null){
            subscriptionPlan.setMaxDaysPerBook(subscriptionplanDto.getMaxDaysPerBook());
        }

        if(subscriptionplanDto.getDisplayOrder()!=null){
            subscriptionPlan.setDisplayOrder(subscriptionplanDto.getDisplayOrder());
        }

        if(subscriptionplanDto.getIsActive()!=null){
            subscriptionPlan.setIsActive(subscriptionplanDto.getIsActive());
        }

        if(subscriptionplanDto.getIsFeatured()!=null){
            subscriptionPlan.setIsFeatured(subscriptionplanDto.getIsFeatured());
        }
        if(subscriptionplanDto.getBadgeText()!=null){
            subscriptionPlan.setBadgeText(subscriptionplanDto.getBadgeText());
        }
        if(subscriptionplanDto.getUpdatedBy()!=null){
            subscriptionPlan.setUpdatedBy(subscriptionplanDto.getUpdatedBy());
        }
        if(subscriptionplanDto.getAdminNotes()!=null){
            subscriptionPlan.setAdminNotes(subscriptionplanDto.getAdminNotes());
        }
    }
}
