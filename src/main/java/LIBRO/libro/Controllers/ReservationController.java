package LIBRO.libro.Controllers;

import LIBRO.libro.Domain.ReservationStatus;
import LIBRO.libro.Exceptions.BookException;
import LIBRO.libro.Payload.DTO.ReservationDTO;
import LIBRO.libro.Payload.Request.ReservationRequest;
import LIBRO.libro.Payload.Request.ReservationSearchRequest;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;


    @PostMapping()
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest
    ) throws Exception {

        ReservationDTO reservationDTO = reservationService
                .createReservation(reservationRequest);

        return ResponseEntity.ok(reservationDTO);
    }


    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createReservationForUser(
            @PathVariable Long userId,
            @Valid @RequestBody ReservationRequest reservationRequest) {

        ReservationDTO reservation =
                reservationService.createReservationForUser(reservationRequest, userId);

        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable("id") Long id) throws Exception {

        ReservationDTO reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(reservation);
    }


    @PostMapping("/{id}/fulfill")
    public ResponseEntity<ReservationDTO> fulfillReservation(@PathVariable Long id)
            throws BookException, Exception {

        ReservationDTO reservation = reservationService.fulfillReservation(id);
        return ResponseEntity.ok(reservation);
    }


    @GetMapping("/my")
    public ResponseEntity<PageResponse<ReservationDTO>> getMyReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> reservations =
                reservationService.getMyReservations(searchRequest);

        return ResponseEntity.ok(reservations);
    }


    @GetMapping
    public ResponseEntity<PageResponse<ReservationDTO>> searchReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setUserId(userId);
        searchRequest.setBookId(bookId);
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> reservations =
                reservationService.searchReservations(searchRequest);

        return ResponseEntity.ok(reservations);
    }











}
