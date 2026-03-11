package LIBRO.libro.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true , nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Genre genre;


    private String publisher;

    private LocalDate publishDate;

    private String language;

    private String description;

    private Integer pages;

    @Column(nullable = false)
    private Integer totalCopies;


    @Column(nullable = false)
    private Integer availableCopies;

    private BigDecimal price;

    private String CoverImageUrl;


    @Column(nullable = false)
    private boolean active=true;


    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @AssertTrue(message = "available copies cannot exceed totalCopies")
    public boolean isAvailableCopiesValid(){
        if(totalCopies == null ||  availableCopies == null){
            return true;
        }


        return availableCopies <= totalCopies;
    }








}
