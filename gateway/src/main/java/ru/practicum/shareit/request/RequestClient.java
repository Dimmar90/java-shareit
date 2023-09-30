package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(Long requesterId, ItemRequestDto itemRequest) {
        return post("", requesterId, itemRequest);
    }

    public ResponseEntity<Object> getResponses(Long requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<?> getByRequestId(Long requestId, Long requesterId) {
        return get("/" + requestId, requesterId);
    }

    public ResponseEntity<?> getAllItemRequestsOfRequester(Long requesterId) {
        return get("/all", requesterId);
    }

    public ResponseEntity<?> getAllItemRequestsOfRequesterPageable(Long requesterId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", requesterId, parameters);
    }
}