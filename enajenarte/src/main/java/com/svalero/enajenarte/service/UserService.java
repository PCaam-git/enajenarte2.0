package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UserOutDto add(UserInDto userInDto) {
        User user = modelMapper.map(userInDto, User.class);

        // generadas por el sistema
        user.setRole("user");
        user.setActive(true);
        user.setBalance(0);
        user.setRegistrationDate(LocalDate.now());

        User newUser = userRepository.save(user);
        return modelMapper.map(newUser, UserOutDto.class);
    }

    public List<UserOutDto> findAll(String username, String email, String active) {
        List<User> users;
        if (!username.isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCase(username);
        } else if (!email.isEmpty()) {
            users = userRepository.findByEmailContainingIgnoreCase(email);
        } else if (!active.isEmpty()) {
            boolean activeValue = Boolean.parseBoolean(active);
            users = userRepository.findByActive(activeValue);
        } else {
            users = userRepository.findAll();
        }
        return modelMapper.map(users, new TypeToken<List<UserOutDto>>() {}.getType());
    }

    public UserOutDto findById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserOutDto.class);
    }

    public UserOutDto modify(long id, UserInDto userInDto) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        //Generado por el sistema
        String role = existingUser.getRole();
        boolean active = existingUser.isActive();
        float balance = existingUser.getBalance();
        LocalDate registrationDate = existingUser.getRegistrationDate();

        modelMapper.map(userInDto, existingUser);
        existingUser.setId(id);

        existingUser.setRole(role);
        existingUser.setActive(active);
        existingUser.setBalance(balance);
        existingUser.setRegistrationDate(registrationDate);

        User updateUser = userRepository.save(existingUser);
        return modelMapper.map(updateUser, UserOutDto.class);
    }

}
