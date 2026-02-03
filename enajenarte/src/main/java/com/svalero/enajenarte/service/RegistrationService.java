package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.RegistrationNotFoundException;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.RegistrationRepository;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private ModelMapper modelMapper;

    public RegistrationOutDto add(RegistrationInDto registrationInDto) throws UserNotFoundException, WorkshopNotFoundException {
        User user = userRepository.findById(registrationInDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Workshop workshop = workshopRepository.findById(registrationInDto.getWorkshopId())
                .orElseThrow(WorkshopNotFoundException::new);

        Registration registration = modelMapper.map(registrationInDto, Registration.class);
        registration.setUser(user);
        registration.setWorkshop(workshop);

        //Aquí se establecen los datos de sistema
        registration.setRegistrationDate(LocalDate.now());
        registration.setConfirmationCode(UUID.randomUUID().toString());
        registration.setPaid(false);
        registration.setAmountPaid(0);
        registration.setRating(null);

        Registration newRegistration = registrationRepository.save(registration);

        RegistrationOutDto registrationOutDto = modelMapper.map(newRegistration, RegistrationOutDto.class);
        registrationOutDto.setUserId(newRegistration.getUser().getId());
        registrationOutDto.setWorkshopId(newRegistration.getWorkshop().getId());

        return registrationOutDto;
    }

    public void delete(long id) throws RegistrationNotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);
        registrationRepository.delete(registration);
    }

    public List<RegistrationOutDto> findAll(String workshopId, String userId, String isPaid) throws WorkshopNotFoundException, UserNotFoundException {
        List<Registration> registrations;

        if (!workshopId.isEmpty()) {
            long workId = Long.parseLong(workshopId);
            Workshop workshop = workshopRepository.findById(workId)
                    .orElseThrow(WorkshopNotFoundException::new);
            registrations = registrationRepository.findByWorkshop(workshop);
        } else if (!userId.isEmpty()) {
            long usId = Long.parseLong(userId);
            User user = userRepository.findById(usId)
                    .orElseThrow(UserNotFoundException::new);
            registrations = registrationRepository.findByUser(user);
        } else if (!isPaid.isEmpty()) {
            boolean paidValue = Boolean.parseBoolean(isPaid);
            registrations = registrationRepository.findByIsPaid(paidValue);

        } else {
            registrations = registrationRepository.findAll();
        }
        return modelMapper.map(registrations, new TypeToken<List<RegistrationOutDto>>() {}.getType());
        }

        public RegistrationOutDto findById(long id) throws RegistrationNotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        return modelMapper.map(registration, RegistrationOutDto.class);
        }

        public RegistrationOutDto modify(long id, RegistrationInDto registrationInDto) throws RegistrationNotFoundException, UserNotFoundException, WorkshopNotFoundException {
        Registration existingRegistration = registrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        User user = userRepository.findById(registrationInDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Workshop workshop = workshopRepository.findById(registrationInDto.getWorkshopId())
                .orElseThrow(WorkshopNotFoundException::new);

        // Sistema
            LocalDate registrationDate = existingRegistration.getRegistrationDate();
            String confirmationCode = existingRegistration.getConfirmationCode();
            boolean paid = existingRegistration.isPaid();
            float amountPaid = existingRegistration.getAmountPaid();
            Integer rating = existingRegistration.getRating();

            modelMapper.map(registrationInDto, existingRegistration);
            existingRegistration.setId(id);

            existingRegistration.setUser(user);
            existingRegistration.setWorkshop(workshop);

            existingRegistration.setRegistrationDate(registrationDate);
            existingRegistration.setConfirmationCode(confirmationCode);
            existingRegistration.setPaid(paid);
            existingRegistration.setAmountPaid(amountPaid);
            existingRegistration.setRating(rating);

            Registration updateRegistration = registrationRepository.save(existingRegistration);
            return modelMapper.map(updateRegistration, RegistrationOutDto.class);
        }

    }

