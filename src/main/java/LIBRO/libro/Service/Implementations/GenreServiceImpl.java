package LIBRO.libro.Service.Implementations;


import LIBRO.libro.Entities.Genre;
import LIBRO.libro.Exceptions.GenreExceptions;
import LIBRO.libro.Mapper.GenreMapper;
import LIBRO.libro.Payload.DTO.GenreDto;
import LIBRO.libro.Repositeries.GenreRepo;
import LIBRO.libro.Service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepo genreRepo;
    private final GenreMapper genreMapper;

    @Override
    public GenreDto createGenre(GenreDto genreDto) {
//         we need to map dto to correspoinding enitity to save



        Genre genreEntity=genreMapper.toEntity(genreDto);
        Genre savedGenre= genreRepo.save(genreEntity);

        //         convert this saved to dto and return to fronted
        return genreMapper.toDto(savedGenre);

    }

    @Override
    public List<GenreDto> getGenres() {
        return genreRepo.findAll().stream()
                .map(genreMapper::toDto).collect(Collectors.toList());
    }


    @Override
    public GenreDto getGenreById(Long id) throws GenreExceptions {

        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new GenreExceptions("Genre not found"));

        return genreMapper.toDto(genre);
    }

    @Override
    public GenreDto updateGenre(Long id, GenreDto genreDto) throws GenreExceptions {

        Genre existing = genreRepo.findById(id)
                .orElseThrow(() -> new GenreExceptions("Genre not found"));



        genreMapper.updateEntityFromDto(genreDto, existing);
        Genre updated = genreRepo.save(existing);

        return genreMapper.toDto(updated);
    }

//    soft
    @Override
    public void deleteGenre(Long id) throws GenreExceptions {

        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new GenreExceptions("Genre not found"));

        genre.setActive(false);

        genreRepo.save(genre);
    }

//    hard

    @Override
    public void hardDeleteGenre(Long id) throws GenreExceptions {

        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new GenreExceptions("Genre not found"));

        genreRepo.delete(genre);
    }

    @Override
    public List<GenreDto> getAllActiveGenresWithSubgenress() {
        List<Genre> toplevel=genreRepo.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

        return genreMapper.toDtos(toplevel);
    }

    @Override
    public List<GenreDto> getTopLevelGenres() {

        List<Genre> toplevel=genreRepo.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

        return genreMapper.toDtos(toplevel);
    }

    @Override
    public long getTotalActiveGenres() {

        return genreRepo.countByActiveTrue();
    }

    @Override
    public long getBookCountByGenre(Long id) {

        return 2;
    }
}