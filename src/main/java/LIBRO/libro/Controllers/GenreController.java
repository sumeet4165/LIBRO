package LIBRO.libro.Controllers;

import LIBRO.libro.Exceptions.GenreExceptions;
import LIBRO.libro.Payload.DTO.GenreDto;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreDto> addGenre(@RequestBody GenreDto genreDto) {

        GenreDto created = genreService.createGenre(genreDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {

        List<GenreDto> genres = genreService.getGenres();

        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id)
            throws GenreExceptions {

        GenreDto genre = genreService.getGenreById(id);

        return ResponseEntity.ok(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(
            @PathVariable Long id,
            @RequestBody GenreDto genreDto)
            throws GenreExceptions {

        GenreDto updated = genreService.updateGenre(id, genreDto);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteGenre(@PathVariable Long id)
            throws GenreExceptions {

        genreService.deleteGenre(id);

        ApiResponse response = new ApiResponse(
                "Genre deleted - soft delete",
                true
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ApiResponse> hardDeleteGenre(@PathVariable Long id)
            throws GenreExceptions {

        genreService.hardDeleteGenre(id);

        ApiResponse response = new ApiResponse(
                "Genre deleted - hard delete",
                true
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-level")
    public ResponseEntity<List<GenreDto>> getTopLevel() {

        return ResponseEntity.ok(genreService.getTopLevelGenres());
    }

    @GetMapping("/total-active")
    public ResponseEntity<Long> getTotalActive() {

        return ResponseEntity.ok(genreService.getTotalActiveGenres());
    }

    @GetMapping("/{id}/book-count")
    public ResponseEntity<Long> getBookCountByGenre(@PathVariable Long id) {

        return ResponseEntity.ok(genreService.getBookCountByGenre(id));
    }
}