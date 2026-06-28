package LIBRO.libro.Service;


import LIBRO.libro.Payload.DTO.BookReviewDTO;
import LIBRO.libro.Payload.Request.CreateReviewRequest;
import LIBRO.libro.Payload.Request.UpdateReviewRequest;
import LIBRO.libro.Payload.Response.PageResponse;

public interface BookReviewService {

    BookReviewDTO createReview(CreateReviewRequest request);

    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);

    PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size);
}