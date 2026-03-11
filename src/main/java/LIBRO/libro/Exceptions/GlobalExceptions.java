package LIBRO.libro.Exceptions;


import LIBRO.libro.Payload.DTO.GenreDto;
import LIBRO.libro.Payload.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptions {


    @ExceptionHandler(GenreExceptions.class)
    public ResponseEntity<ApiResponse> handleGenreException(GenreExceptions e) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(),false));
    }
}
