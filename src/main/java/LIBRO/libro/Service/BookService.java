package LIBRO.libro.Service;

import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Payload.DTO.BookDto;
import LIBRO.libro.Payload.Request.BookSearchRequest;
import LIBRO.libro.Payload.Response.PageResponse;

import java.util.List;

public interface BookService {

    BookDto createBook(BookDto bookDto) throws BookException;

    List<BookDto> createBooksInBulk(List<BookDto> books) throws BookException;

    BookDto getBookById(Long bookId) throws BookException;

    BookDto getBookByISBN(String isbn) throws BookException;

    BookDto updateBook(Long bookId, BookDto bookDto) throws BookException;

    void deleteBook(Long bookId) throws BookException;

    void hardDeleteBook(Long bookId) throws BookException;

    PageResponse<BookDto> searchBooksWithFilters(BookSearchRequest searchRequest);

    long getTotalActiveBooks();

    long getTotalAvailableBooks();
}