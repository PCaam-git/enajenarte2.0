package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.RegistrationController;
import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.RegistrationNotFoundException;
import com.svalero.enajenarte.service.RegistrationService;
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

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // 20X
    @Test
    public void testGetAll() throws Exception {
        List<RegistrationOutDto> registrationsOutDto = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), true, 1, 20, 5, 2L, 10L)
        );

        when(registrationService.findAll("", "", "")).thenReturn(registrationsOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<RegistrationOutDto> registrationsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(registrationsListResponse);
        assertEquals(2, registrationsListResponse.size());
        assertEquals(2, registrationsListResponse.getFirst().getNumberOfTickets());
    }

    // 20X + FILTRO
    @Test
    public void testGetAllByUserId() throws Exception {
        List<RegistrationOutDto> registrationsOutDto = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(3L, LocalDate.of(2026, 1, 12), true, 1, 20, 4, 1L, 11L)
        );

        when(registrationService.findAll("1", "", "")).thenReturn(registrationsOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations")
                                .queryParam("userId", "1")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<RegistrationOutDto> registrationsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(registrationsListResponse);
        assertEquals(2, registrationsListResponse.size());
        assertEquals(1L, registrationsListResponse.getFirst().getUserId());
    }

    // 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(registrationService.findById(99L)).thenThrow(new RegistrationNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    //  400
    // invalid: must register at least 1 person
    @Test
    public void testAdd_BadRequest() throws Exception {

        RegistrationInDto invalidRegistration = new RegistrationInDto(0, 1L, 10L);

        String body = objectMapper.writeValueAsString(invalidRegistration);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/registrations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }
}
