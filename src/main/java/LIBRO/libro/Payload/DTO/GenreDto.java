package LIBRO.libro.Payload.DTO;

import LIBRO.libro.Entities.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class GenreDto {

    private Long id;


    @NotBlank(message = "genre code is mandatory")
    private String code;


    @NotBlank(message = "genree name is required ")
    private String name;



    @Size(max = 500 , message = " Description must not exceed 500 characters ")
    private String description;


    @Min(message = "display order cannot be negative", value = 0)
    private Integer displayOrder=0;


    private Boolean active=true;

    private Long parentGenreId;
    private String parentGenreName;


    private List<GenreDto> subGenres;

//    book cnt
    private Long bookCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;




}
