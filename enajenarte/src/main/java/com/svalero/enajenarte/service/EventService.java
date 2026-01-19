package com.svalero.enajenarte.service;

import ch.qos.logback.core.joran.event.EndEvent;
import com.svalero.enajenarte.domain.Event;
import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.dto.EventInDto;
import com.svalero.enajenarte.dto.EventOutDto;
import com.svalero.enajenarte.exception.EventNotFoundException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.EventRepository;
import com.svalero.enajenarte.repository.SpeakerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SpeakerRepository speakerRepository;
    @Autowired
    private ModelMapper modelMapper;

    // POST
    public EventOutDto add (EventInDto eventInDto)throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(eventInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        Event event = modelMapper.map(eventInDto, Event.class);
        event.setSpeaker(speaker);

        Event newEvent = eventRepository.save(event);
        return modelMapper.map(newEvent, EventOutDto.class);
    }

    // DELETE
    public void delete(long id) throws EventNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        eventRepository.delete(event);
    }

    public List<EventOutDto> findAll(String title, String location, String isPublic) {
        List<Event> events;

        if (!title.isEmpty()) {
            events = eventRepository.findByTitleContainingIgnoreCase(title);
        } else if (!location.isEmpty()){
            events = eventRepository.findByLocationContainingIgnoreCase(location);
        } else if (!isPublic.isEmpty()) {
            boolean publicValue = Boolean.parseBoolean(isPublic);
            events = eventRepository.findByIsPublic(publicValue);
        } else {
            events = eventRepository.findAll();
        }
        return modelMapper.map(events, new TypeToken<List<EventOutDto>>() {}.getType());
    }

    public EventOutDto findById(long id) throws EventNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        return modelMapper.map(event, EventOutDto.class);
    }

    public EventOutDto modify(long id, EventInDto eventInDto) throws EventNotFoundException, SpeakerNotFoundException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);
        Speaker speaker = speakerRepository.findById(eventInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        modelMapper.map(eventInDto, existingEvent);
        existingEvent.setId(id);
        existingEvent.setSpeaker(speaker);

        Event updateEvent = eventRepository.save(existingEvent);
        return modelMapper.map(updateEvent, EventOutDto.class);

    }
}
