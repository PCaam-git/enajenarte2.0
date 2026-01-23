package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import com.svalero.enajenarte.service.WorkshopService;
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
public class WorkshopServiceTests {

    @InjectMocks
    private WorkshopService workshopService;

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() throws Exception {
        List<Workshop> mockWorkshopList = List.of(
                new Workshop(
                        1L,
                        "Oratoria básica",
                        "Taller de oratoria y comunicación",
                        LocalDate.of(2026, 2, 10),
                        90,
                        25,
                        20,
                        true,
                        null,
                        null
                ),
                new Workshop(
                        2L,
                        "Arte terapia",
                        "Taller creativo para autocuidado",
                        LocalDate.of(2026, 3, 5),
                        120,
                        30,
                        15,
                        false,
                        null,
                        null
                )
        );

        List<WorkshopOutDto> modelMapperOut = List.of(
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

        when(workshopRepository.findAll()).thenReturn(mockWorkshopList);
        when(modelMapper.map(mockWorkshopList, new TypeToken<List<WorkshopOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<WorkshopOutDto> actualWorkshopList = workshopService.findAll("", "", "");

        assertEquals(2, actualWorkshopList.size());
        assertEquals("Oratoria básica", actualWorkshopList.getFirst().getName());
        assertEquals("Arte terapia", actualWorkshopList.getLast().getName());

        verify(workshopRepository, times(1)).findAll();
        verify(workshopRepository, times(0)).findByNameContainingIgnoreCase("");
        verify(workshopRepository, times(0)).findByIsOnline(true);
    }

    @Test
    public void testFindAllByName() throws Exception {
        List<Workshop> mockWorkshopList = List.of(
                new Workshop(
                        1L,
                        "Arte terapia",
                        "Taller creativo para autocuidado",
                        LocalDate.of(2026, 3, 5),
                        120,
                        30,
                        15,
                        false,
                        null,
                        null
                ),
                new Workshop(
                        2L,
                        "Arte terapia avanzada",
                        "Taller creativo avanzado",
                        LocalDate.of(2026, 3, 20),
                        120,
                        35,
                        15,
                        false,
                        null,
                        null
                )
        );

        List<WorkshopOutDto> modelMapperOut = List.of(
                new WorkshopOutDto(
                        1L,
                        "Arte terapia",
                        "Taller creativo para autocuidado",
                        LocalDate.of(2026, 3, 5),
                        120,
                        30,
                        false,
                        1L
                ),
                new WorkshopOutDto(
                        2L,
                        "Arte terapia avanzada",
                        "Taller creativo avanzado",
                        LocalDate.of(2026, 3, 20),
                        120,
                        35,
                        false,
                        1L
                )
        );

        when(workshopRepository.findByNameContainingIgnoreCase("arte")).thenReturn(mockWorkshopList);
        when(modelMapper.map(mockWorkshopList, new TypeToken<List<WorkshopOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<WorkshopOutDto> actualWorkshopList = workshopService.findAll("arte", "", "");

        assertEquals(2, actualWorkshopList.size());
        assertEquals("Arte terapia", actualWorkshopList.getFirst().getName());
        assertEquals("Arte terapia avanzada", actualWorkshopList.getLast().getName());

        verify(workshopRepository, times(0)).findAll();
        verify(workshopRepository, times(1)).findByNameContainingIgnoreCase("arte");
    }
}
