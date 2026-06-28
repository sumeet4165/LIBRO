package LIBRO.libro.Mapper;

import LIBRO.libro.Entities.BookLoan;
import LIBRO.libro.Payload.DTO.BookLoanDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class BookLoanMapper {

    public BookLoanDto toDTO(BookLoan bookLoan) {

        if (bookLoan == null) {
            return null;
        }

        BookLoanDto dto = new BookLoanDto();
        dto.setId(bookLoan.getId());

        // User information
        if (bookLoan.getUser() != null) {
            dto.setUserId(bookLoan.getUser().getId());
            dto.setUserName(bookLoan.getUser().getFullName());
            dto.setUserEmail(bookLoan.getUser().getEmail());
        }

        // Book information
        if (bookLoan.getBook() != null) {
            dto.setBookId(bookLoan.getBook().getId());
            dto.setBookTitle(bookLoan.getBook().getTitle());
            dto.setBookIsbn(bookLoan.getBook().getIsbn());
            dto.setBookAuthor(bookLoan.getBook().getAuthor());
            dto.setBookCoverImage(bookLoan.getBook().getCoverImageUrl());
        }

        // Loan details
        dto.setType(bookLoan.getType());
        dto.setStatus(bookLoan.getStatus());
        dto.setCheckoutDate(bookLoan.getCheckoutDate());
        dto.setDueDate(bookLoan.getDueDate());

        dto.setRemainingDays(
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        bookLoan.getDueDate()
                )
        );

        dto.setReturnDate(bookLoan.getReturnDate());
        dto.setRenewalCount(bookLoan.getRenewalCount());
        dto.setMaxRenewals(bookLoan.getMaxRenewals());
        dto.setNotes(bookLoan.getNotes());

        dto.setIsOverdue(bookLoan.isOverdue());
        dto.setOverdueDays(bookLoan.getOverdueDays());

        dto.setCreatedAt(bookLoan.getCreatedAt());
        dto.setUpdatedAt(bookLoan.getUpdatedAt());

        return dto;
    }
}