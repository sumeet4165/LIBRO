package LIBRO.libro.Repositeries;


import LIBRO.libro.Domain.FineStatus;
import LIBRO.libro.Domain.FineType;
import LIBRO.libro.Entities.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepo  extends JpaRepository<Fine, Long> {


    @Query("""
        SELECT f FROM Fine f
        WHERE (:userId IS NULL OR f.user.id = :userId)
          AND (:status IS NULL OR f.status = :status)
          AND (:type IS NULL OR f.type = :type)
        ORDER BY f.createdAt DESC
    """)
    Page<Fine> findAllWithFilters(
            @Param("userId") Long userId,
            @Param("status") FineStatus status,
            @Param("type") FineType type,
            Pageable pageable
    );

    List<Fine> findByUserId(Long userId);

    List<Fine> findByUserIdAndType(Long userId, FineType type);
}
