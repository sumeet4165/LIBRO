package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Payload.Request.CheckoutRequest;
import LIBRO.libro.Service.BookLoanService;
import LIBRO.libro.Service.ReservationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class LoanReservationCoordinator {

    private final BookLoanService bookLoanService;
    private final ReservationService reservationService;

    public LoanReservationCoordinator(@Lazy BookLoanService bookLoanService,
                                      @Lazy ReservationService reservationService) {
        this.bookLoanService = bookLoanService;
        this.reservationService = reservationService;
    }

    public void handleBookReturn(Long bookId) {
        reservationService.assignAvailableBook(bookId);
    }

    public void checkoutReservedBook(Long userId, CheckoutRequest request) throws Exception {
        bookLoanService.checkoutBookForUser(userId, request);
    }
    
    public boolean checkAndFulfillReservation(Long userId, Long bookId) {
        return reservationService.checkAndFulfillReservation(userId, bookId);
    }
}
