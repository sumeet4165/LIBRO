package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Domain.ReservationStatus;
import LIBRO.libro.Domain.UserRole;
import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.Reservation;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Mapper.ReservationMapper;
import LIBRO.libro.Payload.DTO.ReservationDTO;
import LIBRO.libro.Payload.Request.CheckoutRequest;
import LIBRO.libro.Payload.Request.ReservationRequest;
import LIBRO.libro.Payload.Request.ReservationSearchRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Repositeries.BookLoanRepo;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Repositeries.ReservationRepo;
import LIBRO.libro.Service.BookLoanService;
import LIBRO.libro.Service.ReservationService;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceimpl implements ReservationService {

   private  final ReservationRepo reservationRepo;
   private final UserService userService;
    private final BookLoanRepo bookLoanRepo;
    private final BookRepo bookRepo;
    private final ReservationMapper reservationMapper;
    private final LoanReservationCoordinator coordinator;

    int MAX_RESERVATIONS=5;


    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) {
        User user=userService.getCurrentUser();
        return createReservationForUser(reservationRequest,user.getId());
    }

    @Override
    public ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) {

        boolean alreadyHasLoan = bookLoanRepo
                .existsByUserIdAndBookIdAndStatus(
                        userId,
                        reservationRequest.getBookId(),
                        BookLoanStatus.CHECKED_OUT
                );

        if (alreadyHasLoan) {
            throw new RuntimeException("you already have loan on this book");
        }

        // 1. validate user exist
        User user = userService.getCurrentUser();

        // 2. validate book exist
        Book book = bookRepo.findById(reservationRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("book not found"));

        // 3. Check if user already has reservation
        if (reservationRepo.hasActiveReservation(userId, book.getId())) {
            throw new RuntimeException("you have already reservation on this book");
        }

        // 4. Check if book is already available
        if (book.getAvailableCopies() > 0) {
            throw new RuntimeException("book is already available");
        }

        // 5. check user's active reservation limit
        long activeReservations =
                reservationRepo.countActiveReservationsByUser(userId);

        if (activeReservations >= MAX_RESERVATIONS) {
            throw new RuntimeException(
                    "you have reserved " + MAX_RESERVATIONS + " times"
            );
        }

        // 6. create reservation
        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNotificationSent(false);
        reservation.setNotes(reservationRequest.getNotes());

        long pendingCount =
                reservationRepo.countPendingReservationsByBook(book.getId());

        reservation.setQueuePosition((int) pendingCount + 1);


       Reservation saved= reservationRepo.save(reservation);


       return reservationMapper.toDTO(saved);

    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) throws Exception {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new Exception("Reservation not found with ID: " + reservationId));

        // Verify current user owns this reservation (unless admin)
        User currentUser = userService.getCurrentUser();

        if (
                !reservation.getUser().getId().equals(currentUser.getId())
                        && currentUser.getRole() != UserRole.ROLE_ADMIN
        ) {
            throw new Exception("You can only cancel your own reservations");
        }

        if (!reservation.canBeCancelled()) {
            throw new Exception("Reservation cannot be cancelled (current status: "
                    + reservation.getStatus() + ")");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepo.save(reservation);


        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO fulfillReservation(Long reservationId) throws Exception {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new Exception("Reservation not found with ID: " + reservationId));

        if (reservation.getBook().getAvailableCopies() <= 0) {
            throw new Exception("Reservation is not available for pickup (current status: "
                    + reservation.getStatus() + ")");
        }

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setFulfilledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepo.save(reservation);



        CheckoutRequest request = new CheckoutRequest();
        request.setBookId(reservation.getBook().getId());
        request.setNotes("Assign Booked by Admin");

        coordinator.checkoutReservedBook(reservation.getUser().getId(), request);


        return reservationMapper.toDTO(savedReservation);
    }




    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) {
        User  user = userService.getCurrentUser();
        searchRequest.setUserId(user.getId());
        return searchReservations(searchRequest);
    }





    @Override
    public PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest) {

        Pageable pageable = createPageable(searchRequest);

        Page<Reservation> reservationPage = reservationRepo.searchReservationsWithFilters(
                searchRequest.getUserId(),
                searchRequest.getBookId(),
                searchRequest.getStatus(),
                searchRequest.getActiveOnly() != null ? searchRequest.getActiveOnly() : false,
                pageable
        );

        return buildPageResponse(reservationPage);
    }



    private PageResponse<ReservationDTO> buildPageResponse(Page<Reservation> reservationPage) {

        List<ReservationDTO> dtos = reservationPage.getContent()
                .stream()
                .map(reservationMapper::toDTO)
                .toList();

        PageResponse<ReservationDTO> response = new PageResponse<>();

        response.setContent(dtos);
        response.setPageNumber(reservationPage.getNumber());
        response.setPageSize(reservationPage.getSize());
        response.setTotalElements(reservationPage.getTotalElements());
        response.setTotalPages(reservationPage.getTotalPages());
        response.setLast(reservationPage.isLast());

        return response;
    }

    private Pageable createPageable(ReservationSearchRequest searchRequest) {

        String sortBy = searchRequest.getSortBy();
        String safeSortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "reservedAt" : sortBy;

        Sort sort = "ASC".equalsIgnoreCase(searchRequest.getSortDirection())
                ? Sort.by(safeSortBy).ascending()
                : Sort.by(safeSortBy).descending();

        return PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                sort
        );
    }

    @Override
    public void assignAvailableBook(Long bookId) {
        while (true) {
            Book book = bookRepo.findById(bookId).orElse(null);
            if (book == null || book.getAvailableCopies() <= 0) {
                break;
            }
            Reservation nextReservation = reservationRepo.findFirstByBookIdAndStatusOrderByReservedAtAsc(bookId, ReservationStatus.PENDING);
            if (nextReservation == null) {
                break;
            }
            nextReservation.setStatus(ReservationStatus.AVAILABLE);
            nextReservation.setAvailableAt(LocalDateTime.now());
            // Give the user 2 days to pick up the book
            nextReservation.setAvailableUntil(LocalDateTime.now().plusDays(2));
            reservationRepo.save(nextReservation);
            
            // Deduct one from book available copies to truly reserve it
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepo.save(book);
            
            System.out.println("Assigned book " + bookId + " to reservation " + nextReservation.getId());
        }
    }
    
    @Override
    public boolean checkAndFulfillReservation(Long userId, Long bookId) {
        Reservation reservation = reservationRepo.findFirstByBookIdAndStatusOrderByReservedAtAsc(bookId, ReservationStatus.AVAILABLE);
        if (reservation != null && reservation.getUser().getId().equals(userId)) {
            reservation.setStatus(ReservationStatus.FULFILLED);
            reservation.setFulfilledAt(LocalDateTime.now());
            reservationRepo.save(reservation);
            return true;
        }
        return false;
    }
}
