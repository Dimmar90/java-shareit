package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private ItemMapper itemMapper;

    private Item createItem(String name, String description, Boolean available) {
        Item item = new Item();
        item.setId(1L);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(1L);
        item.setAvailable(available);
        return item;
    }

    private List<ItemDto> createListItemsDto(List<Item> itemsList) {
        List<ItemDto> itemsDtoList = new ArrayList<>();
        for (Item item : itemsList) {
            itemsDtoList.add(itemMapper.toItemDto(item));
        }
        return itemsDtoList;
    }

    public User createOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("ownerName");
        owner.setEmail("owner@email");
        return owner;
    }

    public User createBooker() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("bookerName");
        booker.setEmail("booker@email");
        return booker;
    }

    private Booking createBooking() {
        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setItemId(1L);
        expectedBooking.setStart(LocalDateTime.of(2023, 10, 20, 10, 00));
        expectedBooking.setEnd(LocalDateTime.of(2023, 10, 21, 10, 00));
        expectedBooking.setBooker(createBooker());
        expectedBooking.setItem(createItem("itemName", "itemDescription", true));
        expectedBooking.setStatus(BookingStatus.WAITING);
        return expectedBooking;
    }

    private List<Booking> createBookingList(Booking booking) {
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);
        return bookingsList;
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItemId(1L);
        comment.setText("Comment");
        return comment;
    }

    private List<Comment> createCommentList(Comment createComment) {
        ArrayList<Comment> commentList = new ArrayList<>();
        commentList.add(createComment);
        return commentList;
    }

    @Test
    void addItemTest() {
        Item expectedItem = createItem("itemName", "itemDescription", true);
        User owner = createOwner();
        Mockito.when(userRepository.findById(expectedItem.getOwner())).thenReturn(Optional.of(owner));

        Item actualItem = itemService.addItem(owner.getId(), expectedItem);

        assertEquals(actualItem, expectedItem);
    }

    @Test
    void updateItemTest() {
        Item oldItem = createItem("oldItemName", "oldItemDescription", true);
        Item newItem = createItem("newItemName", "newItemDescription", false);
        Mockito.when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findOwnerById(oldItem.getOwner())).thenReturn(oldItem.getOwner());

        Item actualItem = itemService.updateItem(newItem.getOwner(), newItem.getId(), newItem);

        assertEquals(newItem, actualItem);
    }

    @Test
    void updateItemWithEmptyNameTest() {
        Item oldItem = createItem("oldItemName", "oldItemDescription", true);
        Item newItem = createItem(null, "newItemDescription", false);
        Mockito.when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findOwnerById(oldItem.getOwner())).thenReturn(oldItem.getOwner());
        Mockito.when(itemRepository.findNameById(oldItem.getId())).thenReturn(oldItem.getName());

        Item actualItem = itemService.updateItem(newItem.getOwner(), newItem.getId(), newItem);

        assertEquals(newItem, actualItem);
    }

    @Test
    void updateItemWithEmptyDescriptionTestTest() {
        Item oldItem = createItem("oldItemName", "oldItemDescription", true);
        Item newItem = createItem("newItemName", null, false);
        Mockito.when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findOwnerById(oldItem.getOwner())).thenReturn(oldItem.getOwner());
        Mockito.when(itemRepository.findDescriptionById(oldItem.getId())).thenReturn(oldItem.getDescription());

        Item actualItem = itemService.updateItem(newItem.getOwner(), newItem.getId(), newItem);

        assertEquals(newItem, actualItem);
    }

    @Test
    void updateItemWithEmptyAvailableTest() {
        Item oldItem = createItem("oldItemName", "oldItemDescription", true);
        Item newItem = createItem("newItemName", "newItemDescription", null);
        Mockito.when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.findOwnerById(oldItem.getOwner())).thenReturn(oldItem.getOwner());
        Mockito.when(itemRepository.findAvailableById(oldItem.getId())).thenReturn(oldItem.getAvailable());

        Item actualItem = itemService.updateItem(newItem.getOwner(), newItem.getId(), newItem);
        assertEquals(newItem, actualItem);
    }

    @Test
    void updateItemWithWrongOwnerId() {
        Item newItem = createItem("newItemName", "newItemDescription", false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(newItem.getOwner(), newItem.getId(), newItem)
        );

        assertEquals("User has not access to update item", exception.getMessage());
    }

    @Test
    void getItemTest() {
        Item expectedItem = createItem("itemName", "itemDescription", true);
        List<Booking> bookings = createBookingList(createBooking());
        List<Comment> comments = createCommentList(createComment());
        Mockito.when(itemRepository.findById(expectedItem.getId())).thenReturn(Optional.of(expectedItem));
        Mockito.when(bookingRepository.findLastItemBooking(expectedItem.getId(), expectedItem.getOwner())).thenReturn(bookings);
        Mockito.when(commentRepository.findCommentByItemId(expectedItem.getId())).thenReturn(comments);

        ItemDto actualItem = itemService.getItem(expectedItem.getOwner(), expectedItem.getId());
        System.out.println(actualItem);
        assertEquals(itemMapper.toItemDto(expectedItem), actualItem);
    }

    @Test
    void getUsersItemsTest() {
        Item item = createItem("itemName", "itemDescription", true);
        List<Comment> comments = new ArrayList<>();
        item.setComments(comments);
        User owner = createOwner();
        List<Long> itemsIdsList = new ArrayList<>();
        itemsIdsList.add(item.getOwner());
        List<Item> listOfUserItems = new ArrayList<>();
        listOfUserItems.add(item);
        List<ItemDto> expectedListOfUserItemsDto = createListItemsDto(listOfUserItems);
        Mockito.when(userRepository.findById(item.getOwner())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findIdByOwner(item.getOwner())).thenReturn(itemsIdsList);
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        List<ItemDto> actualUserItemsDtoList = itemService.getUserItems(owner.getId());

        assertEquals(expectedListOfUserItemsDto, actualUserItemsDtoList);
    }

    @Test
    void searchItemTest() {
        Item item = createItem("itemName", "itemDescription", true);
        List<Comment> comments = createCommentList(createComment());
        item.setComments(comments);
        List<Item> searchingItems = new ArrayList<>();
        searchingItems.add(item);
        List<ItemDto> expectedSearchingItemsDtoList = createListItemsDto(searchingItems);
        Mockito.when(itemRepository.searchItemByNameOrDescription(anyString())).thenReturn(searchingItems);
        Mockito.when(commentRepository.findCommentByItemId(item.getId())).thenReturn(comments);

        List<ItemDto> actualListOfSearchingItemsDto = itemService.searchItem("itemName");

        assertEquals(expectedSearchingItemsDtoList, actualListOfSearchingItemsDto);
    }

    @Test
    void searchItemWithEmptyTextTest() {
        List<ItemDto> expectedSearchingItemsDtoList = new ArrayList<>();

        List<ItemDto> actualListOfSearchingItemsDto = itemService.searchItem("");

        assertEquals(expectedSearchingItemsDtoList, actualListOfSearchingItemsDto);
    }

    @Test
    void validateItemWithNoAvailableTest() {
        Item item = createItem("itemName", "itemDescription", null);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.validateItem(item)
        );

        assertEquals("Not found item available", exception.getMessage());
    }

    @Test
    void validateItemWithNoNameTest() {
        Item item = createItem("", "itemDescription", true);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.validateItem(item)
        );

        assertEquals("Not found item name", exception.getMessage());
    }

    @Test
    void validateItemWithNoDescriptionTest() {
        Item item = createItem("itemName", null, true);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.validateItem(item)
        );

        assertEquals("Not found item description", exception.getMessage());
    }

    @Test
    void addCommentTest() {
        Item item = createItem("itemName", "itemDescription", true);
        User owner = createOwner();
        Comment expectedComment = createComment();
        Mockito.when(userRepository.findById(item.getOwner())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.countUserBookingsOfItem(owner.getId(), item.getId())).thenReturn(1);

        Comment actualComment = itemService.addComment(owner.getId(), item.getId(), createComment());

        assertEquals(expectedComment.getText(), actualComment.getText());
    }

    @Test
    void addCommentFromWrongUserTest() {
        Item item = createItem("itemName", "itemDescription", true);
        User owner = createOwner();
        Mockito.when(userRepository.findById(item.getOwner())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.countUserBookingsOfItem(owner.getId(), item.getId())).thenReturn(0);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(owner.getId(), item.getId(), createComment())
        );

        assertEquals("User cant add comment", exception.getMessage());
    }

    @Test
    void addEmptyCommentTest() {
        Item item = createItem("itemName", "itemDescription", true);
        User owner = createOwner();
        Comment expectedComment = createComment();
        expectedComment.setText("");
        Mockito.when(userRepository.findById(item.getOwner())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(owner.getId(), item.getId(), expectedComment)
        );

        assertEquals("Comment cant be empty", exception.getMessage());
    }
}