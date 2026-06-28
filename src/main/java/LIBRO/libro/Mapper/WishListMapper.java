package LIBRO.libro.Mapper;

import LIBRO.libro.Entities.Wishlist;
import LIBRO.libro.Payload.DTO.WishlistDTO;
import org.springframework.stereotype.Component;

@Component
public class WishListMapper {
    private final BookMapper bookMapper;

    public WishListMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public WishlistDTO toDTO(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());

        if (wishlist.getUser() != null) {
            dto.setUserId(wishlist.getUser().getId());
            dto.setUserFullName(wishlist.getUser().getFullName());
        }

        if (wishlist.getBook() != null) {
            dto.setBook(bookMapper.toBookDto(wishlist.getBook()));
        }

        dto.setAddedAt(wishlist.getAddedAt());
        dto.setNotes(wishlist.getNotes());

        return dto;
    }
}

