package LIBRO.libro.Repositeries;


import LIBRO.libro.Entities.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscroiptionPlanrepo  extends JpaRepository<SubscriptionPlan, Long> {
    Boolean existsByPlanCode(String planCode);

    SubscriptionPlan findByPlanCode(String planCode);
}
