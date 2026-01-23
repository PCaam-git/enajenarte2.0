package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.UserController;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // 20X
    @Test
    public void testGetAll() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(2L, "mario", "mario@mail.com", "Mario User", "user")
        );

        when(userService.findAll("", "", "")).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(usersListResponse);
        assertEquals(2, usersListResponse.size());
        assertEquals("patricia", usersListResponse.getFirst().getUsername());
    }

    // 20X + FILTRO
    @Test
    public void testGetAllByUsername() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(3L, "patricia.dev", "patricia.dev@mail.com", "Patricia Dev", "user")
        );

        when(userService.findAll("patricia", "", "")).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .queryParam("username", "patricia")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(usersListResponse);
        assertEquals(2, usersListResponse.size());
        assertEquals("patricia", usersListResponse.getFirst().getUsername());
    }

    // 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    //  400
    // username vacío
    @Test
    public void testAdd_BadRequest() throws Exception {

        UserInDto invalidUser = new UserInDto("", "password", "user@mail.com", "Invalid User", 25);

        String body = objectMapper.writeValueAsString(invalidUser);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }
}
