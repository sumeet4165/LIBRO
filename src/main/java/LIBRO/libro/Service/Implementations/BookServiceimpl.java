package LIBRO.libro.Service.Implementations;


import LIBRO.libro.Entities.Book;
import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Mapper.BookMapper;
import LIBRO.libro.Payload.DTO.BookDto;
import LIBRO.libro.Payload.Request.BookSearchRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BookServiceimpl implements BookService {

    private final BookRepo bookRepo;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) throws BookException {

        if(bookRepo.existsByIsbn(bookDto.getIsbn())){
            throw new BookException("Book with ISBN already exists");
        }

        Book book=bookMapper.toBook(bookDto);

//        total copies ==10 and availabe 11 we need to restrict

        book.isAvailableCopiesValid();

        return bookMapper.toBookDto(bookRepo.save(book));



    }

    @Override
    public List<BookDto> createBooksInBulk(List<BookDto> bookDtos) throws BookException {

        List<BookDto> createdBooks=new ArrayList<>();

         for(BookDto bookdto:bookDtos){
            BookDto currbookdto= createBook(bookdto);
            createdBooks.add(currbookdto);


         }
         return createdBooks;
    }

    @Override
    public BookDto getBookById(Long bookId) throws BookException {
        Book book=bookRepo.findById(bookId).orElseThrow(()-> new BookException("Book not found"));

        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto getBookByISBN(String isbn) throws BookException {
        Book book=bookRepo.findByIsbn(isbn).orElseThrow(()-> new BookException("Book not found"));

        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto updateBook(Long bookId, BookDto bookDto) throws BookException {
        Book existing=bookRepo.findById(bookId).orElseThrow(()-> new BookException("Book not found"));
        bookMapper.updateEntity(bookDto, existing);

        existing.isAvailableCopiesValid();

        bookRepo.save(existing);
        return bookMapper.toBookDto(existing);

    }

    @Override
    public void deleteBook(Long bookId) throws BookException {

        Book existing=bookRepo.findById(bookId).orElseThrow(()-> new BookException("Book not found"));

        existing.setActive(false);
        bookRepo.save(existing);

    }

    @Override
    public void hardDeleteBook(Long bookId) throws BookException {
        Book existing=bookRepo.findById(bookId).orElseThrow(()-> new BookException("Book not found"));

        bookRepo.delete(existing);

    }

    @Override
    public PageResponse<BookDto> searchBooksWithFilters(BookSearchRequest searchRequest) {
        Pageable pageable=createPageable(searchRequest.getPage(), searchRequest.getPageSize(), searchRequest.getSortBy(),searchRequest.getSortDirection());

        Page<Book> bookpage=bookRepo.searchBooksWithFilters(
                searchRequest.getSearchTerm(),
                searchRequest.getGenreId(),
                searchRequest.getAvailableOnly(),
                pageable
        );

        return coverttoPageResponse(bookpage);


    }

    @Override
    public long getTotalActiveBooks() {
        return bookRepo.countByActiveTrue();
    }

    @Override
    public long getTotalAvailableBooks() {
        return bookRepo.countAvailableBooks();
    }

//     heloer methid
    private Pageable createPageable(int currpage, int size , String sortBy, String sortDirection ) {

        size=Math.min(size,10);
        size=Math.max(size,1);

        Sort sort=sortDirection.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(currpage,size,sort);



    }


    private PageResponse<BookDto> coverttoPageResponse(Page<Book> bookpage){
        List<BookDto> bookDtos=bookpage.getContent().stream().map(bookMapper::toBookDto).toList();

        return new PageResponse<>(bookDtos,bookpage.getNumber(),bookpage.getSize(),bookpage.getTotalElements(),bookpage.getTotalElements(),bookpage.isLast(),bookpage.isFirst(),bookpage.isEmpty());




    }
}
