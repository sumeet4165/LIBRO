package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Domain.BookLoanStatus;
import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.BookLoan;
import LIBRO.libro.Entities.BookReview;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Mapper.BookReviewMapper;
import LIBRO.libro.Payload.DTO.BookReviewDTO;
import LIBRO.libro.Payload.Request.CreateReviewRequest;
import LIBRO.libro.Payload.Request.UpdateReviewRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Repositeries.BookLoanRepo;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Repositeries.BookReviewRepo;
import LIBRO.libro.Service.BookReviewService;
import LIBRO.libro.Service.BookService;
import LIBRO.libro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReviewServiceimpl implements BookReviewService {

    private final UserService userService;
    private final BookReviewRepo bookReviewRepo;
    private final BookRepo bookRepo;
    private final BookReviewMapper bookReviewMapper;
    private final BookLoanRepo bookLoanRepo;


    public BookReviewDTO createReview(CreateReviewRequest request) {
        // 1. get logged-in user
        User user = userService.getCurrentUser();

        // 2. validate book exists
        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found!"));

        // 3. check duplicate review
        if (bookReviewRepo.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new RuntimeException("Book review already exists!");
        }

        // 4. check if user has read the book
        boolean hasReadBook = hasUserReadBook(user.getId(), book.getId());
        if (!hasReadBook) {
            throw new RuntimeException("You have not read this book!");
        }

        // 5. create review
        BookReview review = new BookReview();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setTitle(request.getTitle());

        BookReview saved = bookReviewRepo.save(review);

        return bookReviewMapper.toDTO(saved);
    }

    private boolean hasUserReadBook(Long userId, Long bookId) {
        return bookLoanRepo.existsByUserIdAndBookIdAndStatus(userId, bookId, BookLoanStatus.RETURN);
    }


    @Override
    public BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) {

        // 1. fetch logged-in user
        User user = userService.getCurrentUser();

        // 2. find review
        BookReview bookReview = bookReviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found!"));

        // 3. authorization check (VERY IMPORTANT 🔥)
        if (!bookReview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to update this review!");
        }

        // 4. update fields
        bookReview.setRating(request.getRating());
        bookReview.setReviewText(request.getReviewText());
        bookReview.setTitle(request.getTitle());

        // 5. save
        BookReview savedBookReview = bookReviewRepo.save(bookReview);

        // 6. return DTO
        return bookReviewMapper.toDTO(savedBookReview);
    }


    @Override
    public void deleteReview(Long reviewId) {

        // 1. get logged-in user
        User currentUser = userService.getCurrentUser();

        // 2. find review
        BookReview bookReview = bookReviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        // 3. authorization check 🔥
        if (!bookReview.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own reviews");
        }



        bookReviewRepo.delete(bookReview);
    }

    @Override
    public PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) {

        // 1. validate book exists
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found by id!"));

        // 2. pagination + sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        // 3. fetch reviews (IMPORTANT: filter active)
        Page<BookReview> reviewPage =
                bookReviewRepo.findByBook(book, pageable);

        // 4. convert to DTO response
        return convertToPageResponse(reviewPage);
    }

    private PageResponse<BookReviewDTO> convertToPageResponse(Page<BookReview> reviewPage) {

        List<BookReviewDTO> reviewDTOs = reviewPage.getContent()
                .stream()
                .map(bookReviewMapper::toDTO)   // ⚠️ use mapper (not bookReviewMapper if field name is mapper)
                .collect(Collectors.toList());

        return new PageResponse<>(
                reviewDTOs,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast(),
                reviewPage.isFirst(),
                reviewPage.isEmpty()
        );
    }




}
