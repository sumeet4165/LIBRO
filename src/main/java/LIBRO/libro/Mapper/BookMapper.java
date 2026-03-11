package LIBRO.libro.Mapper;


import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.Genre;
import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Payload.DTO.BookDto;
import LIBRO.libro.Repositeries.GenreRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepo genreRepo;

    public BookDto toBookDto(Book book) {

        if(book == null) return null;

        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .isbn(book.getIsbn())
                .genreName(book.getGenre().getName())
                .genreCode(book.getGenre().getCode())
                .genreId(book.getGenre().getId())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .description(book.getDescription())
                .language(book.getLanguage())
                .pages(book.getPages())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .coverImageUrl(book.getCoverImageUrl())
                .active(book.isActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();


    }

    public Book toBook(BookDto bookDto) throws BookException {
        if(bookDto == null) return null;

        Book book=Book.builder()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .price(bookDto.getPrice())
                .isbn(bookDto.getIsbn())
                .publisher(bookDto.getPublisher())
                .publishDate(bookDto.getPublishDate())
                .description(bookDto.getDescription())
                .language(bookDto.getLanguage())
                .pages(bookDto.getPages())
                .totalCopies(bookDto.getTotalCopies())
                .availableCopies(bookDto.getAvailableCopies())
                .active(bookDto.getActive())
                .createdAt(bookDto.getCreatedAt())
                .updatedAt(bookDto.getUpdatedAt())
                .CoverImageUrl(bookDto.getCoverImageUrl())
                .build();

        if(bookDto.getGenreId()!=null) {
                Genre genre=genreRepo.findById(bookDto.getGenreId()).orElseThrow(()->new BookException("Genre not found "));
                book.setGenre(genre);

        }

        return book;


    }

    public void updateEntity(BookDto bookDto, Book book) throws BookException {
        if(bookDto == null || book ==null ) return;

//        isbn should not be updated

        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPrice(bookDto.getPrice());
        book.setPublisher(bookDto.getPublisher());
        book.setPublishDate(bookDto.getPublishDate());
        book.setDescription(bookDto.getDescription());
        book.setLanguage(bookDto.getLanguage());
        book.setPages(bookDto.getPages());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getAvailableCopies());
        book.setCoverImageUrl(bookDto.getCoverImageUrl());
        book.setUpdatedAt(bookDto.getUpdatedAt());
        book.setUpdatedAt(bookDto.getUpdatedAt());

        if(bookDto.getGenreId()!=null) {
            Genre genre=genreRepo.findById(bookDto.getGenreId()).orElseThrow(()->new BookException("Genre not found "));
            book.setGenre(genre);

        }

        if(bookDto.getActive()!=null) {
            book.setActive(bookDto.getActive());
        }





    }

}
