package LIBRO.libro.Controllers;

import LIBRO.libro.Payload.DTO.BookReviewDTO;
import LIBRO.libro.Payload.Request.CreateReviewRequest;
import LIBRO.libro.Payload.Request.UpdateReviewRequest;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Service.BookReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<BookReviewDTO> createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        BookReviewDTO reviewDTO = bookReviewService.createReview(request);
        return ResponseEntity.ok(reviewDTO);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<BookReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        BookReviewDTO reviewDTO = bookReviewService.updateReview(id, request);
        return ResponseEntity.ok(reviewDTO);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) throws Exception {

        bookReviewService.deleteReview(reviewId);

        return ResponseEntity.ok(
                new ApiResponse("Review deleted successfully", true)
        );
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10")  int size) {

        PageResponse<BookReviewDTO> reviews =
                bookReviewService.getReviewsByBookId(bookId, page, size);

        return ResponseEntity.ok(reviews);
    }
}



