package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.WorkshopInDto;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private SpeakerRepository speakerRepository;
    @Autowired
    private ModelMapper modelMapper;

    public WorkshopOutDto add(WorkshopInDto workshopInDto) throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(workshopInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        Workshop workshop = modelMapper.map(workshopInDto, Workshop.class);
        workshop.setSpeaker(speaker);

        Workshop newWorkshop = workshopRepository.save(workshop);
        return modelMapper.map(newWorkshop, WorkshopOutDto.class);
    }

    public void delete(long id) throws WorkshopNotFoundException {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);

        workshopRepository.delete(workshop);
    }

    public List<WorkshopOutDto> findAll(String name, String isOnline, String speakerId) throws SpeakerNotFoundException {
        List<Workshop> workshops;

        if(!name.isEmpty()) {
            workshops = workshopRepository.findByNameContainingIgnoreCase(name);
        } else if(!isOnline.isEmpty()) {
            boolean onlineValue = Boolean.parseBoolean(isOnline);
            workshops = workshopRepository.findByIsOnline(onlineValue);
        } else if(!speakerId.isEmpty()) {
            long speakId = Long.parseLong(speakerId);
            Speaker speaker = speakerRepository.findById(speakId)
                    .orElseThrow(SpeakerNotFoundException::new);
            workshops = workshopRepository.findBySpeaker(speaker);
        } else {
            workshops = workshopRepository.findAll();
        }
        return modelMapper.map(workshops, new TypeToken<List<WorkshopOutDto>>() {}.getType());
    }

    public WorkshopOutDto findById(long id) throws WorkshopNotFoundException {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);
        return modelMapper.map(workshop, WorkshopOutDto.class);
    }

    public WorkshopOutDto modify(long id, WorkshopInDto workshopInDto) throws WorkshopNotFoundException, SpeakerNotFoundException {
        Workshop existingWorkshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);
        Speaker speaker = speakerRepository.findById(workshopInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        modelMapper.map(workshopInDto, existingWorkshop);
        existingWorkshop.setId(id);
        existingWorkshop.setSpeaker(speaker);

        Workshop updateWorkshop = workshopRepository.save(existingWorkshop);
        return modelMapper.map(updateWorkshop, WorkshopOutDto.class);
    }
}
