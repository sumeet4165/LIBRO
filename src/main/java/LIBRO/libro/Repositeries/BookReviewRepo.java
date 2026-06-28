package LIBRO.libro.Repositeries;

import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReviewRepo extends JpaRepository<BookReview, Long> {
    Page<BookReview> findByBook(Book book, Pageable pageable);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

}
