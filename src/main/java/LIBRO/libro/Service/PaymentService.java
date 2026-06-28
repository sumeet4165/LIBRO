package LIBRO.libro.Service;

import LIBRO.libro.Payload.DTO.PaymentDto;
import LIBRO.libro.Payload.Request.PaymentInitiateRequest;
import LIBRO.libro.Payload.Request.PaymentVerifyRequest;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws Exception;
    PaymentDto verifyPayment(PaymentVerifyRequest request) throws Exception;
    Page<PaymentDto> getAllPayments(Pageable pageable);


}
