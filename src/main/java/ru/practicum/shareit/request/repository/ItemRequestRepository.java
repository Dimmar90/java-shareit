package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT i FROM ItemRequest AS i " +
            "WHERE i.requesterId = ?1")
    List<ItemRequest> findItemRequestsOfUser(Long requesterId);

    @Query("SELECT i FROM Item AS i " +
            "WHERE i.requestId = ?1")
    List<Item> findRequestItemsList(Long requestId);

    Optional<ItemRequest> findRequestItemById(Long requestId);

    @Query("SELECT i FROM ItemRequest AS i")
    Page<ItemRequest> findItemRequestsPageable(Pageable pageableSize);

    @Query("SELECT i FROM ItemRequest AS i")
    List<ItemRequest> findAllItemRequests();

    @Query("SELECT i FROM ItemRequest AS i " +
            "WHERE i.requesterId != ?1")
    Page<ItemRequest> findItemRequestsOfOtherUsersPageable(Pageable pageableSize, Long requesterId);

}
