package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;


    public ItemRequestDto(Long id, String description, UserDto requester, LocalDateTime created, List<ItemDto> items) {
        this.id = id;
        this.description = description;
        this.requester = requester;
        this.created = created;
        this.items = items;
    }
}
