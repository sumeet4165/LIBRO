package LIBRO.libro.Service.Implementations;

import LIBRO.libro.Entities.Book;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Entities.Wishlist;
import LIBRO.libro.Mapper.WishListMapper;
import LIBRO.libro.Payload.DTO.WishlistDTO;
import LIBRO.libro.Payload.Response.PageResponse;
import LIBRO.libro.Repositeries.BookRepo;
import LIBRO.libro.Repositeries.WishlistRepo;
import LIBRO.libro.Service.UserService;
import LIBRO.libro.Service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepo wishlistRepo;
    private final UserService userService;
    private final BookRepo bookRepo;
    private final WishListMapper wishListMapper;


    @Override
        public WishlistDTO addToWishlist(Long bookId, String notes) throws Exception {

            User user = userService.getCurrentUser();

            // 1. validate book exist
            Book book = bookRepo.findById(bookId)
                    .orElseThrow(() -> new Exception("Book not found"));

            // 2. check if book is already in wishlist
//         for adding ti wishlist

            if (wishlistRepo.existsByUserIdAndBookId(user.getId(), bookId)) {
                throw new Exception("book is already in your wishlist");
            }

            // create wishlist
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setBook(book);
            wishlist.setNotes(notes);

            Wishlist wishlistSaved = wishlistRepo.save(wishlist);

            return wishListMapper.toDTO(wishlistSaved);

        }


    @Override
    public void removeFromWishlist(Long bookId) throws Exception {
        User user = userService.getCurrentUser();

        Wishlist wishlist = wishlistRepo.findByUserIdAndBookId(
                user.getId(),
                bookId
        );

        if (wishlist == null) {
            throw new Exception("book is not in your wishlist");
        }

        wishlistRepo.delete(wishlist);
    }




    @Override
    public PageResponse<WishlistDTO> getMyWishlist(int page, int size) {

        Long userId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("addedAt").descending()
        );

        Page<Wishlist> wishlistPage = wishlistRepo.findByUserId(userId, pageable);

        return convertToPageResponse(wishlistPage);
    }

    private PageResponse<WishlistDTO> convertToPageResponse(Page<Wishlist> wishlistPage) {

        List<WishlistDTO> wishlistDTOs = wishlistPage.getContent()
                .stream()
                .map(wishListMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                wishlistDTOs,
                wishlistPage.getNumber(),
                wishlistPage.getSize(),
                wishlistPage.getTotalElements(),
                wishlistPage.getTotalPages(),
                wishlistPage.isLast(),
                wishlistPage.isFirst(),
                wishlistPage.isEmpty()
        );
    }
}
