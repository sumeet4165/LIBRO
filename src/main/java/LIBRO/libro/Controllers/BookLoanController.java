package LIBRO.libro.Controllers;


import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Payload.DTO.BookLoanDto;
import LIBRO.libro.Payload.Request.BookLoanSearchRequest;
import LIBRO.libro.Payload.Request.CheckinRequest;
import LIBRO.libro.Payload.Request.CheckoutRequest;
import LIBRO.libro.Payload.Request.RenewalRequest;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
public class BookLoanController {
    private final BookLoanService bookLoanService;


    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutBook(
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {

        BookLoanDto bookLoan = bookLoanService.checkoutBook(checkoutRequest);

        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<?> checkoutBookForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {

        BookLoanDto bookLoan =
                bookLoanService.checkoutBookForUser(userId, checkoutRequest);

        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(
            @Valid @RequestBody CheckinRequest checkinRequest) throws Exception {

        BookLoanDto bookLoan = bookLoanService.checkinBook(checkinRequest);

        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }


    @PostMapping("/renew")
    public ResponseEntity<?> renew(
            @Valid @RequestBody RenewalRequest renewalRequest) throws Exception {

        BookLoanDto bookLoan =
                bookLoanService.renewCheckout(renewalRequest);

        return new ResponseEntity<>(bookLoan, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookLoans(
            @RequestParam(required = false) BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws Exception {

        PageResponse<BookLoanDto> bookLoans =
                bookLoanService.getMyBookLoans(status, page, size);

        return ResponseEntity.ok(bookLoans);
    }


    @PostMapping("/search")
    public ResponseEntity<?> getAllBookLoans(
            @RequestBody BookLoanSearchRequest searchRequest) throws Exception {

        PageResponse<BookLoanDto> bookLoans =
                bookLoanService.getBookLoans(searchRequest);

        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/admin/update-overdue")
    public ResponseEntity<?> updateOverdueBookLoans() {

        int updateCount = bookLoanService.updateOverdueBookLoan();

        return ResponseEntity.ok(
                new ApiResponse("overdue book loans are updated", true)
        );
    }














}
