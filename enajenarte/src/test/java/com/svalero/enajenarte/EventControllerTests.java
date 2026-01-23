package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.EventController;
import com.svalero.enajenarte.dto.EventInDto;
import com.svalero.enajenarte.dto.EventOutDto;
import com.svalero.enajenarte.exception.EventNotFoundException;
import com.svalero.enajenarte.service.EventService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;


    @Test
    public void testGetAll() throws Exception {
        List<EventOutDto> eventsOutDto = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L),
                new EventOutDto(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0), 10, true, 1L)
        );

        when(eventService.findAll("", "", "")).thenReturn(eventsOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(eventsListResponse);
        assertEquals(2, eventsListResponse.size());
        assertEquals("Mindfulness", eventsListResponse.getFirst().getTitle());
    }


    @Test
    public void testGetAllByTitle() throws Exception {
        List<EventOutDto> eventsOutDto = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L),
                new EventOutDto(2L, "Mindfulness avanzado", "Zaragoza", LocalDateTime.of(2026, 2, 15, 10, 0), 0, true, 1L)
        );

        when(eventService.findAll("mind", "", "")).thenReturn(eventsOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .queryParam("title", "mind")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(eventsListResponse);
        assertEquals(2, eventsListResponse.size());
        assertEquals("Mindfulness", eventsListResponse.getFirst().getTitle());
    }

    // 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(eventService.findById(99L)).thenThrow(new EventNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/events/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    // 400
    @Test
    public void testAdd_BadRequest() throws Exception {
        // title vacío → @NotEmpty => 400
        EventInDto invalidEvent = new EventInDto("", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 20, 1L);

        String body = objectMapper.writeValueAsString(invalidEvent);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/events")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }
}
