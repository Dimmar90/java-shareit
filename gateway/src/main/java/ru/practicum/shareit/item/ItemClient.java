package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long ownerId, ItemDto item) {
        return patch("/" + itemId, ownerId, item);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getUserItems(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String searchingText) {
        Map<String, Object> parameters = Map.of(
                "text", searchingText
        );
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<?> addComment(Long userId, Long itemId, Comment comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}

