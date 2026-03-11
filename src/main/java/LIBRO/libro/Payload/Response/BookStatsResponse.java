package LIBRO.libro.Payload.Response;

/**
 * Statistics response DTO
 */
public class BookStatsResponse {

    public long totalActiveBooks;
    public long totalAvailableBooks;

    public BookStatsResponse(long totalActiveBooks, long totalAvailableBooks) {
        this.totalActiveBooks = totalActiveBooks;
        this.totalAvailableBooks = totalAvailableBooks;
    }
}