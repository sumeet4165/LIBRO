package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Entities.SubscriptionPlan;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Exceptions.SubscriptionPlanException;
import LIBRO.libro.Mapper.SubscriptionPlanMapper;
import LIBRO.libro.Payload.DTO.SubscriptionplanDto;
import LIBRO.libro.Payload.DTO.UserDto;
import LIBRO.libro.Repositeries.SubscroiptionPlanrepo;
import LIBRO.libro.Service.SubscriptionPlanService;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlansimpl implements SubscriptionPlanService {

    private final SubscroiptionPlanrepo subscroiptionPlanrepo;

    private final SubscriptionPlanMapper  subscriptionPlanMapper;
    private final UserService userService;

    @Override
    public SubscriptionplanDto createSubscription(SubscriptionplanDto subs) throws SubscriptionPlanException {

        if(subscroiptionPlanrepo.existsByPlanCode(subs.getPlanCode())){
            throw new SubscriptionPlanException("Plant code already Exists") ;
        }

        User curruser=userService.getCurrentUser();


        SubscriptionPlan plan =subscriptionPlanMapper.toEntity(subs);
        plan.setCreatedBy(curruser.getFullName());
        plan.setUpdatedBy(curruser.getFullName());
        return subscriptionPlanMapper.toDto(subscroiptionPlanrepo.save(plan));

    }

    @Override
    public SubscriptionplanDto updateSubscription(Long id, SubscriptionplanDto subs) throws SubscriptionPlanException {

        SubscriptionPlan existingPlan = subscroiptionPlanrepo.findById(id).orElseThrow(()->new SubscriptionPlanException("plan not found"));

        subscriptionPlanMapper.update(existingPlan,subs);

        User curruser=userService.getCurrentUser();

        existingPlan.setUpdatedBy(curruser.getFullName());

        return subscriptionPlanMapper.toDto(subscroiptionPlanrepo.save(existingPlan));

    }

    @Override
    public void deleteSubscription(Long id) throws SubscriptionPlanException {

        SubscriptionPlan existingPlan = subscroiptionPlanrepo.findById(id).orElseThrow(()->new SubscriptionPlanException("plan not found"));

        subscroiptionPlanrepo.delete(existingPlan);


    }

    @Override
    public List<SubscriptionplanDto> getAllSubscriptionsPlans() {
        List<SubscriptionPlan> plans=subscroiptionPlanrepo.findAll();
        List<SubscriptionplanDto> subscriptionplanDtos=new ArrayList<>();

        for(SubscriptionPlan plan:plans) {
            subscriptionplanDtos.add(subscriptionPlanMapper.toDto(plan));
        }
        return subscriptionplanDtos;
    }

    @Override
    public SubscriptionplanDto getSubscriptionById(Long id) throws SubscriptionPlanException {

        SubscriptionPlan existingPlan = subscroiptionPlanrepo.findById(id).orElseThrow(()->new SubscriptionPlanException("plan not found"));
        return subscriptionPlanMapper.toDto(existingPlan);

    }

    @Override
    public SubscriptionPlan getBySubscriptionplancode(String subscriptionplancode) throws SubscriptionPlanException {
      SubscriptionPlan subscriptionPlan= subscroiptionPlanrepo.findByPlanCode(subscriptionplancode);
      if(subscriptionPlan==null){
          throw new SubscriptionPlanException("Subscription Plan not found");

      }
      return subscriptionPlan;

    }



}
