package LIBRO.libro.Service;

import LIBRO.libro.Payload.DTO.ReservationDTO;
import LIBRO.libro.Payload.Request.ReservationRequest;
import LIBRO.libro.Payload.Request.ReservationSearchRequest;
import LIBRO.libro.Payload.Response.PageResponse;


public interface ReservationService {


    ReservationDTO createReservation(ReservationRequest reservationRequest);

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest,
                                            Long userId);

    ReservationDTO cancelReservation(Long reservationId) throws Exception;

    ReservationDTO fulfillReservation(Long reservationId) throws Exception;

    /**
     * Assigns the next pending reservation when a book becomes available.
     * @param bookId the returned book
     */
    void assignAvailableBook(Long bookId);
    
    boolean checkAndFulfillReservation(Long userId, Long bookId);
    /**
     * Get my reservations (current user) with filters
     * @param searchRequest Search criteria
     * @return Paginated reservations
     */
    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest);

    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);
}

