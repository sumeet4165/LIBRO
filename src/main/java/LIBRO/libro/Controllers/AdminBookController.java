package LIBRO.libro.Controllers;

import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Payload.DTO.BookDto;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) throws BookException {
        BookDto created = bookService.createBook(bookDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BookDto>> createBooksInBulk(@RequestBody List<BookDto> books) throws BookException {
        List<BookDto> created = bookService.createBooksInBulk(books);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) throws BookException {
        BookDto updated = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> softDeleteBook(@PathVariable Long id) throws BookException {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book softly deleted successfully", true));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> hardDeleteBook(@PathVariable Long id) throws BookException {
        bookService.hardDeleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book permanently deleted successfully", true));
    }
}
