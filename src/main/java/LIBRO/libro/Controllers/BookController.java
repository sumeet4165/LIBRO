package LIBRO.libro.Controllers;


import LIBRO.libro.Entities.Book;
import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Exceptions.GenreExceptions;
import LIBRO.libro.Payload.DTO.BookDto;
import LIBRO.libro.Payload.Request.BookSearchRequest;
import LIBRO.libro.Payload.Response.ApiResponse;
import LIBRO.libro.Payload.Response.BookStatsResponse;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")

public class BookController {
    private final BookService bookService;


    @PostMapping
    public ResponseEntity<BookDto> createBook( @Valid @RequestBody  BookDto bookDto) throws BookException {

     BookDto created=bookService.createBook(bookDto);

     return ResponseEntity.ok(created);
    }


    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkBook( @Valid @RequestBody List<BookDto> bookDtolist) throws BookException {

          List<BookDto >created=bookService.createBooksInBulk(bookDtolist);

        return ResponseEntity.ok(created);
    }





    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) throws BookException {
        BookDto book=bookService.getBookById(id);
        return ResponseEntity.ok(book);
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

//     one more search method



    @GetMapping
    public ResponseEntity<PageResponse<BookDto>> searchBooks(

            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false , defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {

        // Build search request from query parameters
        BookSearchRequest searchRequest = new BookSearchRequest();

        searchRequest.setGenreId(genreId);
        searchRequest.setAvailableOnly(availableOnly);

        searchRequest.setPage(page);
        searchRequest.setPageSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<BookDto> books =
                bookService.searchBooksWithFilters(searchRequest);

        return ResponseEntity.ok(books);
    }



    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDto>> advancedSearch(@RequestBody BookSearchRequest searchRequest) throws BookException {
        PageResponse<BookDto> books=bookService.searchBooksWithFilters(searchRequest);
        return ResponseEntity.ok(books);

    }

    @GetMapping("/stats")
    public ResponseEntity<BookStatsResponse> getBookStats() throws BookException {
        long totalActive=bookService.getTotalActiveBooks();
        long totalAvailable=bookService.getTotalAvailableBooks();
        BookStatsResponse bookStatsResponse=new BookStatsResponse(totalActive,totalAvailable);
        return ResponseEntity.ok(bookStatsResponse);


    }













}
