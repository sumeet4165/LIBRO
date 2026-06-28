package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.FineStatus;
import LIBRO.libro.Domain.FineType;
import LIBRO.libro.Domain.PaymentGateWay;
import LIBRO.libro.Domain.PaymentType;
import LIBRO.libro.Entities.BookLoan;
import LIBRO.libro.Entities.Fine;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Mapper.FineMapper;
import LIBRO.libro.Payload.DTO.FineDTO;
import LIBRO.libro.Payload.Request.CreateFineRequest;
import LIBRO.libro.Payload.Request.PaymentInitiateRequest;
import LIBRO.libro.Payload.Request.WaiveFineRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Payload.Response.PaymentInitiateResponse;
import LIBRO.libro.Repositeries.BookLoanRepo;
import LIBRO.libro.Repositeries.FineRepo;
import LIBRO.libro.Service.FineService;
import LIBRO.libro.Service.PaymentService;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepo fineRepo;
    private final BookLoanRepo bookLoanRepo;
    private final FineMapper fineMapper;
    private final UserService userService;
    private final PaymentService paymentService;


    @Override
    public FineDTO createFine(CreateFineRequest createFineRequest) {
//        validate book loan exits

        // 1. validate book loan exist
        BookLoan bookLoan = bookLoanRepo
                .findById(createFineRequest.getBookLoanId())
                .orElseThrow(() -> new RuntimeException("Book loan doesn't exist"));

        // 2. create fine
        Fine fine = Fine.builder()
                .bookLoan(bookLoan)
                .user(bookLoan.getUser())
                .type(createFineRequest.getType())
                .amount(createFineRequest.getAmount())
                .status(FineStatus.PENDING)
                .reason(createFineRequest.getReason())
                .notes(createFineRequest.getNotes())
                .build();

        Fine savedFine = fineRepo.save(fine);


        return fineMapper.toDTO(savedFine);
    }


    @Override
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception {

        // 1. validate fine exist
        Fine fine = fineRepo.findById(fineId)
                .orElseThrow(() -> new Exception("Fine doesn't exist"));

        // 2. check already paid
        if (fine.getStatus().equals(FineStatus.PAID)) {
            throw new Exception("fine already paid");
        }

        if (fine.getStatus().equals(FineStatus.WAIVED)) {
            throw new Exception("fine waived");
        }

        // 3. initiate payment
        User user = userService.getCurrentUser();

        PaymentInitiateRequest request = PaymentInitiateRequest
                .builder()
                .userId(user.getId())
                .fineId(fine.getId())
                .paymentType(PaymentType.FINE)
                .gateway(PaymentGateWay.RAZORPAY)
                .amount(fine.getAmount())
                .description("library fine payment")
                .build();

        return paymentService.initiatePayment(request);
    }


    @Override
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception {


        Fine fine = fineRepo.findById(fineId)
                .orElseThrow(() -> new Exception("Fine not found with id: " + fineId));

        // Apply payment amount safely
        fine.applyPayment(amount);

        fine.setTransactionId(transactionId);
        fine.setStatus(FineStatus.PAID);
        fine.setUpdatedAt(LocalDateTime.now());

        fineRepo.save(fine);
    }


    @Override
    public FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception {


        Fine fine = fineRepo.findById(waiveFineRequest.getFineId())
                .orElseThrow(() -> new Exception("Fine not found with id: " + waiveFineRequest.getFineId()));

        // 2. Check if already waived or paid
        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new Exception("Fine has already been waived");
        }

        if (fine.getStatus() == FineStatus.PAID) {
            throw new Exception("Fine has already been paid and cannot be waived");
        }

        // 3. Waive the fine
        User currentAdmin = userService.getCurrentUser();
        fine.waive(currentAdmin, waiveFineRequest.getReason());

        // 4. Save and return
        Fine savedFine = fineRepo.save(fine);


        return fineMapper.toDTO(savedFine);
    }


    @Override
    public List<FineDTO> getMyFines(FineStatus status, FineType type) {


        User currentUser = userService.getCurrentUser();
        List<Fine> fines;

        // Apply filters based on parameters
        if (status != null && type != null) {

            // Both filters
            fines = fineRepo.findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getStatus() == status && f.getType() == type)
                    .collect(Collectors.toList());

        } else if (status != null) {

            // Status filter only
            fines = fineRepo.findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getStatus() == status)
                    .collect(Collectors.toList());

        } else if (type != null) {

            // Type filter only
            fines = fineRepo.findByUserIdAndType(currentUser.getId(), type);

        } else {

            // No filter - all fines for user
            fines = fineRepo.findByUserId(currentUser.getId());
        }

        return fines.stream().map(fineMapper::toDTO).collect(Collectors.toList());
    }


    @Override
    public PageResponse<FineDTO> getAllFines(
            FineStatus status,
            FineType type,
            Long userId,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Fine> finePage = fineRepo.findAllWithFilters(
                userId,
                status,
                type,
                pageable
        );

        return convertToPageResponse(finePage);
    }


    private PageResponse<FineDTO> convertToPageResponse(Page<Fine> finePage) {

        List<FineDTO> fineDTOs = finePage.getContent()
                .stream()
                .map(fineMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                fineDTOs,
                finePage.getNumber(),
                finePage.getSize(),
                finePage.getTotalElements(),
                finePage.getTotalPages(),
                finePage.isLast(),
                finePage.isFirst(),
                finePage.isEmpty()
        );
    }
}
