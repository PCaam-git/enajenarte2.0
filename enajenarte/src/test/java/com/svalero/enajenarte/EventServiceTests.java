package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Event;
import com.svalero.enajenarte.dto.EventOutDto;
import com.svalero.enajenarte.repository.EventRepository;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Event> mockEventList = List.of(
                new Event(
                        1L,
                        "Mindfulness",
                        "Zaragoza",
                        LocalDateTime.of(2026, 2, 1, 10, 0),
                        0,
                        true,
                        30,
                        null
                ),
                new Event(
                        2L,
                        "Arte terapia",
                        "Madrid",
                        LocalDateTime.of(2026, 3, 1, 18, 0),
                        10,
                        true,
                        50,
                        null
                )
        );

        List<EventOutDto> modelMapperOut = List.of(
                new EventOutDto(
                        1L,
                        "Mindfulness",
                        "Zaragoza",
                        LocalDateTime.of(2026, 2, 1, 10, 0),
                        0,
                        true,
                        1L
                ),
                new EventOutDto(
                        2L,
                        "Arte terapia",
                        "Madrid",
                        LocalDateTime.of(2026, 3, 1, 18, 0),
                        10,
                        true,
                        1L
                )
        );

        when(eventRepository.findAll()).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<EventOutDto> actualEventList = eventService.findAll("", "", "");

        assertEquals(2, actualEventList.size());
        assertEquals("Mindfulness", actualEventList.getFirst().getTitle());
        assertEquals("Arte terapia", actualEventList.getLast().getTitle());

        verify(eventRepository, times(1)).findAll();
        verify(eventRepository, times(0)).findByTitleContainingIgnoreCase("");
        verify(eventRepository, times(0)).findByLocationContainingIgnoreCase("");
        verify(eventRepository, times(0)).findByIsPublic(true);
    }

    @Test
    public void testFindAllByTitle() {
        List<Event> mockEventList = List.of(
                new Event(
                        1L,
                        "Mindfulness",
                        "Zaragoza",
                        LocalDateTime.of(2026, 2, 1, 10, 0),
                        0,
                        true,
                        30,
                        null
                ),
                new Event(
                        2L,
                        "Mindfulness avanzado",
                        "Zaragoza",
                        LocalDateTime.of(2026, 2, 15, 10, 0),
                        0,
                        true,
                        20,
                        null
                )
        );

        List<EventOutDto> modelMapperOut = List.of(
                new EventOutDto(
                        1L,
                        "Mindfulness",
                        "Zaragoza",
                        LocalDateTime.of(2026, 2, 1, 10, 0),
                        0,
                        true,
                        1L
                ),
                new EventOutDto(
                        2L,
                        "Mindfulness avanzado",
                        "Zaragoza",
                        LocalDateTime.of(2026, 2, 15, 10, 0),
                        0,
                        true,
                        1L
                )
        );

        when(eventRepository.findByTitleContainingIgnoreCase("mind")).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<EventOutDto> actualEventList = eventService.findAll("mind", "", "");

        assertEquals(2, actualEventList.size());
        assertEquals("Mindfulness", actualEventList.getFirst().getTitle());
        assertEquals("Mindfulness avanzado", actualEventList.getLast().getTitle());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(1)).findByTitleContainingIgnoreCase("mind");
    }
}
