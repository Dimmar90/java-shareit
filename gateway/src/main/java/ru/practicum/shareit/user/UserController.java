package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDto user) {
        if (user.getName() == null) {
            String message = "User name is absent";
            log.error(message);
            throw new BadRequestException(message);
        }
        if (user.getEmail() == null) {
            String message = "User email is absent";
            log.error(message);
            throw new BadRequestException(message);
        }
        return userClient.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid UserDto user,
                                    @PathVariable("id") long id) {
        return userClient.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return userClient.getAllUser();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return userClient.deleteUser(id);
    }
}
