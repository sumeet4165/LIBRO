package LIBRO.libro.Mapper;

import LIBRO.libro.Entities.Genre;
import LIBRO.libro.Payload.DTO.GenreDto;
import LIBRO.libro.Repositeries.GenreRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepo genreRepo;

    public GenreDto toDto(Genre saved) {

        GenreDto dto = GenreDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .code(saved.getCode())
                .description(saved.getDescription())
                .displayOrder(saved.getDisplayOrder())
                .active(saved.isActive())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();

        if (saved.getParentGenre() != null) {
            dto.setParentGenreId(saved.getParentGenre().getId());
            dto.setParentGenreName(saved.getParentGenre().getName());
        }

        if (saved.getSubGenres() != null && !saved.getSubGenres().isEmpty()) {
            dto.setSubGenres(
                    saved.getSubGenres()
                            .stream()
                            .filter(Genre::isActive)
                            .map(this::toDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public Genre toEntity(GenreDto genreDto) {

        if (genreDto == null) return null;

        Genre genreEntity = Genre.builder()
                .name(genreDto.getName())
                .code(genreDto.getCode())
                .description(genreDto.getDescription())
                .displayOrder(genreDto.getDisplayOrder())
                .active(true)
                .build();

        if (genreDto.getParentGenreId() != null) {
            Genre parentGenre = genreRepo.findById(genreDto.getParentGenreId())
                    .orElseThrow(() -> new RuntimeException("Parent genre not found"));

            genreEntity.setParentGenre(parentGenre);
        }

        return genreEntity;
    }

    public void updateEntityFromDto(GenreDto genreDto, Genre existing) {
        if (genreDto == null || existing ==null) return ;

        existing.setName(genreDto.getName());
        existing.setCode(genreDto.getCode());
        existing.setDescription(genreDto.getDescription());
        existing.setDisplayOrder(genreDto.getDisplayOrder()!=null?genreDto.getDisplayOrder():0);
        if(genreDto.getActive() != null){
            existing.setActive(genreDto.getActive());
        }

        if(genreDto.getParentGenreId() != null){

            genreRepo.findById(genreDto.getParentGenreId()).ifPresent(existing::setParentGenre);
        }



    }

    public List<GenreDto> toDtos(List<Genre> genres) {
        return genres.stream().map(this::toDto).collect(Collectors.toList());
    }
}