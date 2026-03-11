package LIBRO.libro.Service;


import LIBRO.libro.Entities.Genre;
import LIBRO.libro.Exceptions.GenreExceptions;
import LIBRO.libro.Payload.DTO.GenreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {

    GenreDto createGenre(GenreDto genre);

    List<GenreDto> getGenres();

    GenreDto getGenreById(Long id) throws GenreExceptions;

    GenreDto updateGenre(Long id, GenreDto genre) throws GenreExceptions;

    void deleteGenre(Long id) throws GenreExceptions;

    void hardDeleteGenre(Long id) throws GenreExceptions;


    List<GenreDto> getAllActiveGenresWithSubgenress();

    List<GenreDto> getTopLevelGenres();

//    Page<GenreDto> searchGenre(String searchGenre, Pageable pageable);


    long getTotalActiveGenres();

    long getBookCountByGenre(Long id);






}
