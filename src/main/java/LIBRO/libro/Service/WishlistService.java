package LIBRO.libro.Service;

import LIBRO.libro.Payload.DTO.WishlistDTO;
import LIBRO.libro.Payload.Response.PageResponse;

public interface WishlistService {
    WishlistDTO addToWishlist(Long bookId, String notes) throws Exception;

    void removeFromWishlist(Long bookId) throws Exception;

    PageResponse<WishlistDTO> getMyWishlist(int page, int size);

}
