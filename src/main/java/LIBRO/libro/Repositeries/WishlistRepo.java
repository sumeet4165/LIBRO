package LIBRO.libro.Repositeries;


import LIBRO.libro.Entities.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepo extends JpaRepository<Wishlist, Long> {
    Page<Wishlist> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);


    Wishlist findByUserIdAndBookId(Long id, Long bookId);

}
