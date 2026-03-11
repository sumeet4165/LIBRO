package LIBRO.libro.Repositeries;


import LIBRO.libro.Entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepo extends JpaRepository<Genre,Long> {
    List<Genre> findByActiveTrueOrderByDisplayOrderAsc();

    //     all subgenree of parent
    List<Genre> findByParentGenreIdAndActiveTrueOrderByDisplayOrderAsc(Long id);


    List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();


    long countByActiveTrue();


//    @Query("""
//            SELECT COUNT(b)
//            FROM Book b
//            WHERE b.genre.id = :genreId
//            """)
//    long countBooksByGenre(Long genreId);
//


}


