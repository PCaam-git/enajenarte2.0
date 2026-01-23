package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.WorkshopController;
import com.svalero.enajenarte.dto.WorkshopInDto;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.service.WorkshopService;
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

@WebMvcTest(WorkshopController.class)
public class WorkshopControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkshopService workshopService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // 20x
    @Test
    public void testGetAll() throws Exception {
        List<WorkshopOutDto> workshopsOutDto = List.of(
                new WorkshopOutDto(
                        1L,
                        "Oratoria básica",
                        "Taller de oratoria y comunicación",
                        LocalDate.of(2026, 2, 10),
                        90,
                        25,
                        true,
                        1L
                ),
                new WorkshopOutDto(
                        2L,
                        "Arte terapia",
                        "Taller creativo para autocuidado",
                        LocalDate.of(2026, 3, 5),
                        120,
                        30,
                        false,
                        1L
                )
        );

        when(workshopService.findAll("", "", "")).thenReturn(workshopsOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<WorkshopOutDto> workshopsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(workshopsListResponse);
        assertEquals(2, workshopsListResponse.size());
        assertEquals("Oratoria básica", workshopsListResponse.getFirst().getName());
    }
    // 20x con filtros
    @Test
    public void testGetAllByName() throws Exception {
        List<WorkshopOutDto> workshopsOutDto = List.of(
                new WorkshopOutDto(2L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, false, 1L),
                new WorkshopOutDto(3L, "Arte terapia avanzada", "Taller creativo avanzado", LocalDate.of(2026, 3, 20), 120, 35, false, 1L)
        );

        when(workshopService.findAll("arte", "", "")).thenReturn(workshopsOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .queryParam("name", "arte")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<WorkshopOutDto> workshopsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(workshopsListResponse);
        assertEquals(2, workshopsListResponse.size());
        assertEquals("Arte terapia", workshopsListResponse.getFirst().getName());
    }

    // 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(workshopService.findById(99L)).thenThrow(new WorkshopNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }
    // 400
    // Name vacío
    @Test
    public void testAdd_BadRequest() throws Exception {
        WorkshopInDto invalidWorkshop = new WorkshopInDto("", "Taller de prueba", LocalDate.of(2026, 2, 10), 90, 25, 20, true, 1L
        );

        String body = objectMapper.writeValueAsString(invalidWorkshop);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/workshops")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }
}
