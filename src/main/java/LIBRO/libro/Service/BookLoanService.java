package LIBRO.libro.Service;


import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Payload.DTO.BookLoanDto;
import LIBRO.libro.Payload.Request.BookLoanSearchRequest;
import LIBRO.libro.Payload.Request.CheckinRequest;
import LIBRO.libro.Payload.Request.CheckoutRequest;
import LIBRO.libro.Payload.Request.RenewalRequest;
import LIBRO.libro.Payload.Response.PageResponse;

public interface BookLoanService {

    BookLoanDto checkoutBook(CheckoutRequest checkoutRequest) throws SubscriptionException, BookException;

    BookLoanDto checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws SubscriptionException, BookException;

    BookLoanDto checkinBook(CheckinRequest checkinRequest) throws Exception;

    BookLoanDto renewCheckout(RenewalRequest renewalRequest) throws Exception;

    PageResponse<BookLoanDto> getMyBookLoans(BookLoanStatus status, int page, int size);

    PageResponse<BookLoanDto> getBookLoans(BookLoanSearchRequest request);

    int updateOverdueBookLoan();
}