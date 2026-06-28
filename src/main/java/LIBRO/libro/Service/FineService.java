package LIBRO.libro.Service;


import LIBRO.libro.Domain.FineStatus;
import LIBRO.libro.Domain.FineType;
import LIBRO.libro.Payload.DTO.FineDTO;
import LIBRO.libro.Payload.Request.CreateFineRequest;
import LIBRO.libro.Payload.Request.WaiveFineRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;

import java.util.List;

public interface FineService {

    FineDTO createFine(CreateFineRequest createFineRequest);

    PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception;

    void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception;

    FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception;

    List<FineDTO> getMyFines(FineStatus status, FineType type);

    PageResponse<FineDTO> getAllFines(
            FineStatus status,
            FineType type,
            Long userId,
            int page,
            int size
    );
}