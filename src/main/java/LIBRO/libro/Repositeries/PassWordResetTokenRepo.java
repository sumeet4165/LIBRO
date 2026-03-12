package LIBRO.libro.Repositeries;


import LIBRO.libro.Entities.ResetPassWordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassWordResetTokenRepo extends JpaRepository<ResetPassWordToken, Long> {
    Optional<ResetPassWordToken> findByToken(String token);

}
