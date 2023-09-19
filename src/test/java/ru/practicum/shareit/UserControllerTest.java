package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private UserMapper userMapper = new UserMapper();

    public User createUser(String name, String email) {
        User user = new User();
        user.setId(1L);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    @SneakyThrows
    void create() {
        User expectedUser = createUser("userName", "user@user.com");
        when(userService.create(expectedUser)).thenReturn(expectedUser);

        String result = mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUser)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUser), result);
    }

    @Test
    @SneakyThrows
    void update() {
        User user = createUser("userName", "user@user.com");
        User expectedUpdatedUser = createUser("updatedUserName", "updated@user.com");
        when(userService.update(expectedUpdatedUser, user.getId())).thenReturn(expectedUpdatedUser);

        String result = mvc.perform(patch("/users/{id}", user.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUpdatedUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUpdatedUser), result);
    }

    @Test
    @SneakyThrows
    void getUser() {
        User user = createUser("userName", "user@user.com");
        UserDto expectedUser = userMapper.toUserDto(user);
        when(userService.getUser(user.getId())).thenReturn(expectedUser);

        String result = mvc.perform(get("/users/{id}", expectedUser.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUser), result);
    }

    @Test
    @SneakyThrows
    void getAllUser() {
        User user = createUser("userName", "user@user.com");
        UserDto userDto = userMapper.toUserDto(user);
        List<UserDto> expectedUsersList = new ArrayList<>();
        expectedUsersList.add(userDto);
        when(userService.getAllUsers()).thenReturn(expectedUsersList);

        String result = mvc.perform(get("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedUsersList)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUsersList), result);
    }
}
