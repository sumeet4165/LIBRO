package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.ReservationStatus;
import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.Reservation;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Repositeries.ReservationRepo;
import LIBRO.libro.Service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepo reservationRepo;
    private final ReservationService reservationService;
    private final BookRepo bookRepo;

    // Run every hour
    @Scheduled(fixedRate = 3600000)
    public void processExpiredReservations() {
        System.out.println("Running scheduled check for expired reservations...");
        
        // Find reservations that are AVAILABLE and past their availableUntil time
        int page = 0;
        int size = 100;
        Page<Reservation> expiredPage;
        
        do {
            expiredPage = reservationRepo.searchReservationsWithFilters(null, null, ReservationStatus.AVAILABLE, true, PageRequest.of(page, size));
            
            for (Reservation reservation : expiredPage.getContent()) {
                if (reservation.getAvailableUntil() != null && LocalDateTime.now().isAfter(reservation.getAvailableUntil())) {
                    System.out.println("Reservation " + reservation.getId() + " has expired. Reassigning...");
                    
                    // Mark as expired
                    reservation.setStatus(ReservationStatus.EXPIRED);
                    reservation.setCancelledAt(LocalDateTime.now());
                    reservation.setNotes("Automatically expired due to pickup time limit.");
                    reservationRepo.save(reservation);
                    
                    // Return the book to the pool temporarily
                    Book book = reservation.getBook();
                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                    bookRepo.save(book);
                    
                    // Immediately try to assign to the next pending person
                    reservationService.assignAvailableBook(book.getId());
                }
            }
            page++;
        } while (expiredPage.hasNext());
    }
}
