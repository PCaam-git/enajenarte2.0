package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<User> mockUserList = List.of(
                new User(1L, "patricia", "pass1", "patricia@mail.com", "Patricia User",
                        LocalDate.now(), 40, true, 0, "user", null),
                new User(2L, "mario", "pass2", "mario@mail.com", "Mario User",
                        LocalDate.now(), 35, true, 0, "user", null)
        );

        List<UserOutDto> modelMapperOut = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(2L, "mario", "mario@mail.com", "Mario User", "user")
        );

        when(userRepository.findAll()).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<UserOutDto> actualUserList = userService.findAll("", "", "");

        assertEquals(2, actualUserList.size());
        assertEquals("patricia", actualUserList.getFirst().getUsername());
        assertEquals("mario", actualUserList.getLast().getUsername());

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(0)).findByUsernameContainingIgnoreCase("");
        verify(userRepository, times(0)).findByEmailContainingIgnoreCase("");
        verify(userRepository, times(0)).findByActive(true);
    }

    @Test
    public void testFindAllByUsername() {
        List<User> mockUserList = List.of(
                new User(1L, "patricia", "pass1", "patricia@mail.com", "Patricia User",
                        null, 40, true, 0, "user", null),
                new User(2L, "patricia.dev", "pass2", "patricia.dev@mail.com", "Patricia Dev",
                        null, 22, true, 0, "user", null)
        );

        List<UserOutDto> modelMapperOut = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(2L, "patricia.dev", "patricia.dev@mail.com", "Patricia Dev", "user")
        );

        when(userRepository.findByUsernameContainingIgnoreCase("patricia")).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<UserOutDto> actualUserList = userService.findAll("patricia", "", "");

        assertEquals(2, actualUserList.size());
        assertEquals("patricia", actualUserList.getFirst().getUsername());
        assertEquals("patricia.dev", actualUserList.getLast().getUsername());

        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("patricia");
    }
}
