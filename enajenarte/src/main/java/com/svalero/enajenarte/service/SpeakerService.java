package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {

    @Autowired
    public SpeakerRepository speakerRepository;
    @Autowired
    public ModelMapper modelMapper;

    public SpeakerOutDto add(SpeakerInDto speakerInDto) {
        Speaker speaker= modelMapper.map(speakerInDto, Speaker.class);

        // generado por el sistema
        speaker.setWorkshopHoursTotal(0);

        Speaker newSpeaker = speakerRepository.save(speaker);
        return modelMapper.map(newSpeaker, SpeakerOutDto.class);
    }

    public void delete(long id) throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);
        speakerRepository.delete(speaker);
    }

    public List<SpeakerOutDto> findAll(String speciality, String available, String yearsExperience) {
        List<Speaker> speakers;

        if(!speciality.isEmpty()) {
            speakers = speakerRepository.findBySpecialityContainingIgnoreCase(speciality);
        } else if(!available.isEmpty()) {
            boolean availableValue = Boolean.parseBoolean(available);
            speakers = speakerRepository.findByAvailable(availableValue);
        } else if (!yearsExperience.isEmpty()) {
            int experience = Integer.parseInt(yearsExperience);
            speakers = speakerRepository.findByYearsExperience(experience);
        } else {
            speakers = speakerRepository.findAll();
        }
        return modelMapper.map(speakers, new TypeToken<List<SpeakerOutDto>>() {}.getType());
    }

    public SpeakerOutDto findById(long id) throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);

        return modelMapper.map(speaker, SpeakerOutDto.class);
    }

    public SpeakerOutDto modify(long id, SpeakerInDto speakerInDto) throws SpeakerNotFoundException {
        Speaker existingSpeaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);

        float currentHours = existingSpeaker.getWorkshopHoursTotal();

        modelMapper.map(speakerInDto, existingSpeaker);
        existingSpeaker.setId(id);
        existingSpeaker.setWorkshopHoursTotal(currentHours);

        Speaker updateSpeaker = speakerRepository.save(existingSpeaker);
        return modelMapper.map(updateSpeaker, SpeakerOutDto.class);
    }
}
