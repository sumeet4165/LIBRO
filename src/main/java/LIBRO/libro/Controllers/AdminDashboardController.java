package LIBRO.libro.Controllers;

import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Domain.ReservationStatus;
import LIBRO.libro.Entities.Subscription;
import LIBRO.libro.Repositeries.BookLoanRepo;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Repositeries.ReservationRepo;
import LIBRO.libro.Repositeries.SubscriptionRepo;
import LIBRO.libro.Service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final BookLoanRepo bookLoanRepo;
    private final ReservationRepo reservationRepo;
    private final BookRepo bookRepo;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepo subscriptionRepo;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Overdue Loans
         long overdueLoans = bookLoanRepo.findOverdueBookLoans(LocalDate.now(), org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
         stats.put("overdueLoans", overdueLoans);

        // 2. Pending Reservations
        long pendingReservations = reservationRepo.searchReservationsWithFilters(null, null, ReservationStatus.PENDING, true, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        stats.put("pendingReservations", pendingReservations);

        // 3. Active Subscriptions
        long activeSubscriptions = subscriptionService.getAllSubscriptions(org.springframework.data.domain.Pageable.unpaged()).stream().filter(s -> s.getIsActive() && !s.getIsExpired()).count();
        stats.put("activeSubscriptions", activeSubscriptions);

        // 4. Total Catalog Books
        long totalBooks = bookRepo.count();
        stats.put("totalBooks", totalBooks);

        // 5. Current Month Earnings (sum price of active subscriptions starting in current month)
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        long currentMonthEarnings = subscriptionRepo.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()) && s.getStartDate() != null
                        && !s.getStartDate().isBefore(firstDayOfMonth)
                        && !s.getStartDate().isAfter(lastDayOfMonth))
                .mapToLong(s -> s.getPrice() != null ? s.getPrice() : 0L)
                .sum();
        stats.put("currentMonthEarnings", currentMonthEarnings);

        return ResponseEntity.ok(stats);
    }
}
