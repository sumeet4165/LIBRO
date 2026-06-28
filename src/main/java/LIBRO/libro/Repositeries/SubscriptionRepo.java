package LIBRO.libro.Repositeries;

import LIBRO.libro.Entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {

    @Query("select s from Subscription s where s.user.id = :userId AND " +
            "s.active = true and " +
            "s.startDate <= :today and s.endDate >= :today")
    Optional<Subscription> findActiveSubscriptionByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );

    @Query("select s from Subscription s where s.active = true " +
            "AND s.endDate < :today")
    List<Subscription> findExpiredActiveSubscriptions(
            @Param("today") LocalDate today
    );
}

