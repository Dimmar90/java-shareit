package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapper();

    public User createUser(String name, String email) {
        User user = new User();
        user.setId(1L);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    void createUserTest() {
        User expectedUser = createUser("userName", "user@email");
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        User actualUser = userService.create(expectedUser);

        assertEquals(actualUser, expectedUser);
    }

    @Test
    void updateUserTest() {
        User oldUser = createUser("oldUserName", "oldUser@email");
        User newUser = createUser("newUserName", "newUser@email");
        Mockito.when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(newUser));

        User updatedUser = userService.update(newUser, oldUser.getId());

        assertEquals(updatedUser, newUser);
    }

    @Test
    void updateUserWithEmptyName() {
        User oldUser = createUser("oldUserName", "oldUser@email");
        User newUser = createUser(null, "newUserEmail");
        String oldUserName = oldUser.getName();
        Mockito.when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(newUser));
        Mockito.when(userRepository.findNameById(oldUser.getId())).thenReturn(oldUserName);

        User updatedUser = userService.update(newUser, oldUser.getId());

        assertEquals(updatedUser, newUser);
    }

    @Test
    void updateUserWithEmptyEmail() {
        User oldUser = createUser("oldUserName", "oldUser@email");
        User newUser = createUser("newUserName", null);
        String oldUserEmail = oldUser.getEmail();
        Mockito.when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(newUser));
        Mockito.when(userRepository.findEmailById(oldUser.getId())).thenReturn(oldUserEmail);

        User updatedUser = userService.update(newUser, oldUser.getId());

        assertEquals(updatedUser, newUser);
    }

    @Test
    void getUserTest() {
        User user = createUser("userName", "userEmail");
        UserDto expectingUser = userMapper.toUserDto(user);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserDto(user)).thenReturn(expectingUser);

        UserDto actualUser = userService.getUser(1L);

        assertEquals(expectingUser, actualUser);
    }

    @Test
    void getAllUsers() {
        User user = createUser("userName", "user@email");
        List<User> listOfUser = new ArrayList<>();
        listOfUser.add(user);
        List<UserDto> expectingListOfUsersDto = new ArrayList<>();
        expectingListOfUsersDto.add(userMapper.toUserDto(listOfUser.get(0)));
        Mockito.when(userRepository.findAll()).thenReturn(listOfUser);

        List<UserDto> actualUsersDtoList = userService.getAllUsers();

        assertEquals(actualUsersDtoList, expectingListOfUsersDto);
    }

    @Test
    void deleteTest() {
        User user = createUser("userName", "user@email");
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.delete(user.getId());
    }
}
