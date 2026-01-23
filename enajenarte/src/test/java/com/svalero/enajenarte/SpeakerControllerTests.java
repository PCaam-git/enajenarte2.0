package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.SpeakerController;
import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.service.SpeakerService;
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

@WebMvcTest(SpeakerController.class)
public class SpeakerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpeakerService speakerService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<SpeakerOutDto> speakersOutDto = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Carlos", "Perez", "carlos@mail.com", "oratoria", 8)
        );

        when(speakerService.findAll("", "", "")).thenReturn(speakersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<SpeakerOutDto> speakersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(speakersListResponse);
        assertEquals(2, speakersListResponse.size());
        assertEquals("Ana", speakersListResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetAllBySpeciality() throws Exception {
        List<SpeakerOutDto> speakersOutDto = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(3L, "Lucia", "Martin", "lucia@mail.com", "mindfulness", 3)
        );

        when(speakerService.findAll("mind", "", "")).thenReturn(speakersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers")
                                .queryParam("speciality", "mind")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<SpeakerOutDto> speakersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(speakersListResponse);
        assertEquals(2, speakersListResponse.size());
        assertEquals("Ana", speakersListResponse.getFirst().getFirstName());
    }

    @Test // 404
    public void testGetById_NotFound() throws Exception {
        when(speakerService.findById(99L)).thenThrow(new SpeakerNotFoundException());

        mockMvc.perform(
                        // ojo: en tu controller es /speaker/{id} (singular)
                        MockMvcRequestBuilders.get("/speaker/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    @Test // 400 // firstName vacío
    public void testAdd_BadRequest() throws Exception {
        // firstName vacío
        SpeakerInDto invalidSpeaker = new SpeakerInDto("", "Lopez", "ana@mail.com", "mindfulness", 5, 10f, true, LocalDate.of(2025, 1, 1)
        );

        String body = objectMapper.writeValueAsString(invalidSpeaker);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/speakers")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

}
