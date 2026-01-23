package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.service.SpeakerService;
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
public class SpeakerServiceTests {

    @InjectMocks
    private SpeakerService speakerService;

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Speaker> mockSpeakerList = List.of(
                new Speaker(
                        1L,
                        "Ana",
                        "Lopez",
                        "ana@mail.com",
                        "mindfulness",
                        5,
                        0,
                        true,
                        LocalDate.of(2025, 1, 1),
                        null
                ),
                new Speaker(
                        2L,
                        "Carlos",
                        "Perez",
                        "carlos@mail.com",
                        "oratoria",
                        8,
                        0,
                        true,
                        LocalDate.of(2024, 5, 10),
                        null
                )
        );

        List<SpeakerOutDto> modelMapperOut = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Carlos", "Perez", "carlos@mail.com", "oratoria", 8)
        );

        when(speakerRepository.findAll()).thenReturn(mockSpeakerList);
        when(modelMapper.map(mockSpeakerList, new TypeToken<List<SpeakerOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<SpeakerOutDto> actualSpeakerList = speakerService.findAll("", "", "");

        assertEquals(2, actualSpeakerList.size());
        assertEquals("Ana", actualSpeakerList.getFirst().getFirstName());
        assertEquals("Carlos", actualSpeakerList.getLast().getFirstName());

        verify(speakerRepository, times(1)).findAll();
        verify(speakerRepository, times(0)).findBySpecialityContainingIgnoreCase("");
        verify(speakerRepository, times(0)).findByAvailable(true);
        verify(speakerRepository, times(0)).findByYearsExperience(5);
    }

    @Test
    public void testFindAllBySpeciality() {
        List<Speaker> mockSpeakerList = List.of(
                new Speaker(
                        1L,
                        "Ana",
                        "Lopez",
                        "ana@mail.com",
                        "mindfulness",
                        5,
                        0,
                        true,
                        LocalDate.of(2025, 1, 1),
                        null
                ),
                new Speaker(
                        2L,
                        "Lucia",
                        "Martin",
                        "lucia@mail.com",
                        "mindfulness",
                        3,
                        0,
                        true,
                        LocalDate.of(2025, 2, 1),
                        null
                )
        );

        List<SpeakerOutDto> modelMapperOut = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Lucia", "Martin", "lucia@mail.com", "mindfulness", 3)
        );

        when(speakerRepository.findBySpecialityContainingIgnoreCase("mind")).thenReturn(mockSpeakerList);
        when(modelMapper.map(mockSpeakerList, new TypeToken<List<SpeakerOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<SpeakerOutDto> actualSpeakerList = speakerService.findAll("mind", "", "");

        assertEquals(2, actualSpeakerList.size());
        assertEquals("Ana", actualSpeakerList.getFirst().getFirstName());
        assertEquals("Lucia", actualSpeakerList.getLast().getFirstName());

        verify(speakerRepository, times(0)).findAll();
        verify(speakerRepository, times(1)).findBySpecialityContainingIgnoreCase("mind");
    }
}
