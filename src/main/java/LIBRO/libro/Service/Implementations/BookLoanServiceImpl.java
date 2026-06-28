package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Domain.BookLoanType;
import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.BookLoan;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Exceptions.SubscriptionException;
import LIBRO.libro.Mapper.BookLoanMapper;
import LIBRO.libro.Payload.DTO.BookLoanDto;
import LIBRO.libro.Payload.DTO.SubscriptionDto;
import LIBRO.libro.Payload.Request.BookLoanSearchRequest;
import LIBRO.libro.Payload.Request.CheckinRequest;
import LIBRO.libro.Payload.Request.CheckoutRequest;
import LIBRO.libro.Payload.Request.RenewalRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Repositeries.BookLoanRepo;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Service.BookLoanService;
import LIBRO.libro.Service.SubscriptionService;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepo bookLoanRepo;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final BookRepo bookRepo;
    private final BookLoanMapper bookLoanMapper;
    private final LIBRO.libro.Service.FineService fineService;
    private final LoanReservationCoordinator coordinator;

    @Override
    public BookLoanDto checkoutBook(CheckoutRequest checkoutRequest) throws SubscriptionException, BookException {

        User user=userService.getCurrentUser();

        return checkoutBookForUser(user.getId(),checkoutRequest);

    }

    @Override
    public BookLoanDto checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws SubscriptionException, BookException {

        User user=userService.findById(userId);

//        validate user has active subscription

        SubscriptionDto subscription=subscriptionService.getUsersActiveSubscription(userId);

//        availabbility of book
        Book book=bookRepo.findById(checkoutRequest.getBookId()).orElseThrow(()->new BookException("book not found"));


//        active or not
        if(!book.isActive()){
            throw new BookException("book is not active");
        }

        boolean hasReservationFulfilled = coordinator.checkAndFulfillReservation(userId, book.getId());

        if(!hasReservationFulfilled && book.getAvailableCopies() <= 0){
            throw new BookException("book has no available copies");
        }

//        already book load on this book

        if(bookLoanRepo.hasActiveCheckout(userId,book.getId())){
            throw new BookException("book has already checkout");
        }

//      users   active checkout limit

        long  activecheckouts=bookLoanRepo.countActiveBookLoansByUser(userId);

        int maxbooksallowed=subscription.getMaxBooksAllowed();

        if(activecheckouts>=maxbooksallowed){
            throw new BookException("you have reached the maximum number of books allowed");

        }

//        overdue books

        long overdueCount=bookLoanRepo.countOverdueBookLoansByUser(userId);

        if(overdueCount>0 ){
            throw new BookException("First return old overdue Books");
        }

//        create book loan
        BookLoan bookLoan=BookLoan.builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .overdueDays(checkoutRequest.getCheckoutDays())
                .dueDate(LocalDate.now().plusDays(checkoutRequest.getCheckoutDays()))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)

                .build();

        if(!hasReservationFulfilled) {
            book.setAvailableCopies(book.getAvailableCopies()-1);
            bookRepo.save(book);
        }


//        save book loan
        BookLoan saved=bookLoanRepo.save(bookLoan);

        return bookLoanMapper.toDTO(saved);








    }

    @Override
    public BookLoanDto checkinBook(CheckinRequest checkinRequest) throws Exception {

//         validate book loan exists
        BookLoan bookLoan=bookLoanRepo.findById(checkinRequest.getBookLoanId()).orElseThrow(()->new Exception("book loan not found"));

//        chekc if already active
        if(!bookLoan.isActive()){
            throw new BookException("BookLoan is not active");
        }

//        set return data

        bookLoan.setReturnDate(LocalDate.now());

        BookLoanStatus condition = checkinRequest.getCondition();

        if (condition == null) {
            condition = BookLoanStatus.RETURN;
        }

        bookLoan.setStatus(condition);

        int overdueDays = 0;
        if (LocalDate.now().isAfter(bookLoan.getDueDate())) {
            bookLoan.setOverdue(true);
            overdueDays = calculateOverdueDate(bookLoan.getDueDate(), LocalDate.now());
            bookLoan.setOverdueDays(overdueDays);
            
            // Generate overdue fine
            LIBRO.libro.Payload.Request.CreateFineRequest fineReq = new LIBRO.libro.Payload.Request.CreateFineRequest();
            fineReq.setBookLoanId(bookLoan.getId());
            fineReq.setAmount((long) overdueDays * 10); // Example: ₹10 per day
            fineReq.setType(LIBRO.libro.Domain.FineType.OVERDUE);
            fineReq.setReason(overdueDays + " days overdue");
            fineService.createFine(fineReq);
        } else {
            bookLoan.setOverdueDays(0);
            bookLoan.setOverdue(false);
        }

        if (condition == BookLoanStatus.LOST) {
            bookLoan.setNotes("Book marked as LOST");
            
            LIBRO.libro.Payload.Request.CreateFineRequest lostFine = new LIBRO.libro.Payload.Request.CreateFineRequest();
            lostFine.setBookLoanId(bookLoan.getId());
            lostFine.setAmount(1000L); // Flat fee or book price
            lostFine.setType(LIBRO.libro.Domain.FineType.LOSS);
            lostFine.setReason("Lost book replacement fee");
            fineService.createFine(lostFine);
            
        } else if (condition == BookLoanStatus.DAMAGED) {
            bookLoan.setNotes("Book returned DAMAGED");
            
            LIBRO.libro.Payload.Request.CreateFineRequest damagedFine = new LIBRO.libro.Payload.Request.CreateFineRequest();
            damagedFine.setBookLoanId(bookLoan.getId());
            damagedFine.setAmount(300L); // Damaged processing fee
            damagedFine.setType(LIBRO.libro.Domain.FineType.DAMAGE);
            damagedFine.setReason("Book damaged processing fee");
            fineService.createFine(damagedFine);
            
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepo.save(book);
            coordinator.handleBookReturn(book.getId());
        } else {
            bookLoan.setNotes("Book returned normally");
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepo.save(book);
            coordinator.handleBookReturn(book.getId());
        }

        BookLoan saved = bookLoanRepo.save(bookLoan);
        return bookLoanMapper.toDTO(saved);


    }

    @Override
    public BookLoanDto renewCheckout(RenewalRequest renewalRequest) throws Exception {

//        validate book loan exits
        BookLoan bookLoan=bookLoanRepo.findById(renewalRequest.getBookLoanId()).orElseThrow(()->new Exception("book loan not found"));
//        check if canbe renewed

        if(!bookLoan.canRenew()){
            throw new BookException("BookLoan is not renewable");
        }

//        update due data
        bookLoan.setDueDate(bookLoan.getDueDate().plusDays(renewalRequest.getExtensionDays()));

        bookLoan.setRenewalCount(bookLoan.getRenewalCount()+1);

        bookLoan.setNotes("book renewed by user");

        BookLoan saved=bookLoanRepo.save(bookLoan);
        return bookLoanMapper.toDTO(saved);



    }

    @Override
    public PageResponse<BookLoanDto> getMyBookLoans(BookLoanStatus status, int page, int size) {


        User currentUser = userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;

        if (status != null) {
            // Return only active checkouts, sorted by due date
            Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
            bookLoanPage = bookLoanRepo.findByStatusAndUser(
                    status,
                    currentUser,
                    pageable
            );
        } else {
            // Return all history (both active and returned), sorted by creation date
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            bookLoanPage = bookLoanRepo.findByUserId(
                    currentUser.getId(),
                    pageable
            );
        }

        return convertToPageResponse(bookLoanPage);
    }




    @Override
    public PageResponse<BookLoanDto> getBookLoans(BookLoanSearchRequest searchRequest) {

        Pageable pageable = createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection()
        );

        Page<BookLoan> bookLoanPage;

        if (Boolean.TRUE.equals(searchRequest.getOverdueOnly())) {

            bookLoanPage = bookLoanRepo.findOverdueBookLoans(
                    LocalDate.now(),
                    pageable
            );

        } else if (searchRequest.getUserId() != null) {

            bookLoanPage = bookLoanRepo.findByUserId(
                    searchRequest.getUserId(),
                    pageable
            );

        } else if (searchRequest.getBookId() != null) {

            bookLoanPage = bookLoanRepo.findByBookId(
                    searchRequest.getBookId(),
                    pageable
            );

        } else if (searchRequest.getStatus() != null) {

            bookLoanPage = bookLoanRepo.findByStatus(
                    searchRequest.getStatus(),
                    pageable
            );

        } else if (searchRequest.getStartDate() != null &&
                searchRequest.getEndDate() != null) {

            bookLoanPage = bookLoanRepo.findBookLoansByDateRange(
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    pageable
            );

        } else {

            bookLoanPage = bookLoanRepo.findAll(pageable);

        }

        return convertToPageResponse(bookLoanPage);
    }



    @Override
    public int updateOverdueBookLoan() {

        Pageable pageable = PageRequest.of(0, 1000); // Process in batches

        Page<BookLoan> overduePage =
                bookLoanRepo.findOverdueBookLoans(LocalDate.now(), pageable);

        int updateCount = 0;

        for (BookLoan bookLoan : overduePage.getContent()) {

            if (bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT) {

                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setOverdue(true);

                // Calculate overdue days
                int overdueDays = calculateOverdueDate(
                        bookLoan.getDueDate(),
                        LocalDate.now()
                );

                bookLoan.setOverdueDays(overdueDays);
                bookLoanRepo.save(bookLoan);
                updateCount++;
            }
        }

        return updateCount;
    }


//    helper

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {

        size = Math.min(size, 100);
        size = Math.max(size, 1);

        String safeSortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt" : sortBy;
        Sort sort = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.by(safeSortBy).ascending()
                : Sort.by(safeSortBy).descending();

        return PageRequest.of(page, size, sort);
    }


    private PageResponse<BookLoanDto> convertToPageResponse(Page<BookLoan> bookLoanPage) {

        List<BookLoanDto> bookLoanDTOs = bookLoanPage.getContent()
                .stream()
                .map(bookLoanMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                bookLoanDTOs,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty()
        );
    }

    private int calculateOverdueDate(LocalDate dueDate, LocalDate today) {

        if (today.isBefore(dueDate) || today.isEqual(dueDate)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(dueDate, today);
    }





}
