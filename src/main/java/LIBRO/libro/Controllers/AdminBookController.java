package LIBRO.libro.Controllers;


import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Payload.DTO.BookDto;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")

public class AdminBookController {
    private final BookService bookService;


    @PostMapping()
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) throws BookException {

        BookDto created=bookService.createBook(bookDto);

        return ResponseEntity.ok(created);
    }


    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkBook( @Valid @RequestBody List<BookDto> bookDtolist) throws BookException {

        List<BookDto >created=bookService.createBooksInBulk(bookDtolist);

        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) throws BookException {
        BookDto updated=bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long id)
            throws BookException {

        bookService.deleteBook(id);

        ApiResponse response = new ApiResponse(
                "Book deleted - soft delete",
                true
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> hardDeleteBook(@PathVariable Long id)
            throws BookException {

        bookService.hardDeleteBook(id);

        ApiResponse response = new ApiResponse(
                "Book deleted - permanent delete",
                true
        );

        return ResponseEntity.ok(response);
    }


}
