package LIBRO.libro.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @NotBlank(message = "genre code is mandatory")
    private String code;


    @NotBlank(message = "genree name is required ")
    private String name;



    @Size(max = 500 , message = " Description must not exceed 500 characters ")
    private String description;


    @Min(message = "display order cannot be negative", value = 0)
    private Integer displayOrder=0;


    @Column(nullable = false)
    private boolean active=true;



    @ManyToOne
    private Genre parentGenre;



    @OneToMany(
            mappedBy = "parentGenre",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Genre> subGenres=new ArrayList<>();

//
//    @OneToMany(mappedBy = "Genre" , cascade = CascadeType.PERSIST)
//    private List<Book> books=new ArrayList<>();


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
