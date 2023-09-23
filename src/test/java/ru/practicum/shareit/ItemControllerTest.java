package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private ItemMapper itemMapper = new ItemMapper();

    private Item createItem(String name, String description, Boolean available) {
        Item item = new Item();
        item.setId(1L);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(1L);
        item.setAvailable(available);
        return item;
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItemId(1L);
        comment.setText("Comment");
        return comment;
    }

    @Test
    @SneakyThrows
    void addItem() {
        Item expectedItem = createItem("itemName", "itemDescription", true);
        when(itemService.addItem(expectedItem.getOwner(), expectedItem)).thenReturn(expectedItem);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", expectedItem.getOwner())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedItem)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedItem), result);
    }

    @Test
    @SneakyThrows
    void updateItem() {
        Item item = createItem("itemName", "itemDescription", true);
        Item expectedUpdatedItem = createItem("newName", "newDescription", false);
        when(itemService.updateItem(item.getOwner(), item.getId(), expectedUpdatedItem)).thenReturn(expectedUpdatedItem);

        String result = mvc.perform(patch("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", item.getOwner())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUpdatedItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUpdatedItem), result);
    }

    @Test
    @SneakyThrows
    void getItem() {
        Item item = createItem("itemName", "itemDescription", true);
        ItemDto expectedItemDto = itemMapper.toItemDto(item);
        when(itemService.getItem(item.getOwner(), item.getId())).thenReturn(expectedItemDto);

        String result = mvc.perform(get("/items/{id}", expectedItemDto.getId())
                        .header("X-Sharer-User-Id", item.getOwner())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedItemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedItemDto), result);
    }

    @Test
    @SneakyThrows
    void getUserItems() {
        Item item = createItem("itemName", "itemDescription", true);
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<ItemDto> expectedListOfUsersItems = new ArrayList<>();
        expectedListOfUsersItems.add(itemDto);
        when(itemService.getUserItems(item.getOwner())).thenReturn(expectedListOfUsersItems);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", item.getOwner())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedListOfUsersItems)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedListOfUsersItems), result);
    }

    @Test
    @SneakyThrows
    void search() {
        Item item = createItem("itemName", "itemDescription", true);
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<ItemDto> expectedListOfSearchingItems = new ArrayList<>();
        expectedListOfSearchingItems.add(itemDto);
        when(itemService.searchItem("itemName")).thenReturn(expectedListOfSearchingItems);

        String result = mvc.perform(get("/items/search?text=itemName")
                        .header("X-Sharer-User-Id", item.getOwner())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedListOfSearchingItems)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedListOfSearchingItems), result);
    }

    @Test
    @SneakyThrows
    void addComment() {
        Item item = createItem("itemName", "itemDescription", true);
        Comment expectedComment = createComment();
        when(itemService.addComment(1L, item.getId(), expectedComment)).thenReturn(expectedComment);

        String result = mvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedComment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedComment), result);
    }
}
