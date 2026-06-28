package LIBRO.libro.Repositeries;


import LIBRO.libro.Entities.Payment;
import LIBRO.libro.Payload.DTO.PaymentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo  extends JpaRepository<Payment, Long> {

}
