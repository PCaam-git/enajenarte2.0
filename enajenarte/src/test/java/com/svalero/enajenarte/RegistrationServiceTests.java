package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.repository.RegistrationRepository;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import com.svalero.enajenarte.service.RegistrationService;
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
public class RegistrationServiceTests {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() throws Exception {
        List<Registration> mockRegistrationList = List.of(
                new Registration(1L, LocalDate.of(2026, 1, 1), "CONF-1", false, 1, 0, null, null, null),
                new Registration(2L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20, 5, null, null)
        );

        List<RegistrationOutDto> modelMapperOut = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 1), false, 1, 0, 0, 0L, 0L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 2), true, 2, 20, 5, 0L, 0L)
        );

        when(registrationRepository.findAll()).thenReturn(mockRegistrationList);
        when(modelMapper.map(mockRegistrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(modelMapperOut);

        List<RegistrationOutDto> actualRegistrationList = registrationService.findAll("", "", "");

        assertEquals(2, actualRegistrationList.size());
        assertEquals("CONF-1", mockRegistrationList.getFirst().getConfirmationCode());
        assertEquals("CONF-2", mockRegistrationList.getLast().getConfirmationCode());

        verify(registrationRepository, times(1)).findAll();
        verify(registrationRepository, times(0)).findByIsPaid(true);
    }

    @Test
    public void testFindAllByIsPaid() throws Exception {
        List<Registration> mockRegistrationList = List.of(
                new Registration(1L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20, 5, null, null),
                new Registration(2L, LocalDate.of(2026, 1, 3), "CONF-3", true, 1, 10, 4, null, null)
        );

        List<RegistrationOutDto> modelMapperOut = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 2), true, 2, 20, 5, 0L, 0L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 3), true, 1, 10, 4, 0L, 0L)
        );

        when(registrationRepository.findByIsPaid(true)).thenReturn(mockRegistrationList);
        when(modelMapper.map(mockRegistrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(modelMapperOut);

        List<RegistrationOutDto> actualRegistrationList = registrationService.findAll("", "", "true");

        assertEquals(2, actualRegistrationList.size());
        assertEquals(true, actualRegistrationList.getFirst().isPaid());

        verify(registrationRepository, times(0)).findAll();
        verify(registrationRepository, times(1)).findByIsPaid(true);
    }
}
