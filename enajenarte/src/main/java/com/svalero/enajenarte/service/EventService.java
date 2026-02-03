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

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speakerId salga a 0
        EventOutDto eventOutDto = modelMapper.map(newEvent, EventOutDto.class);
        if (newEvent.getSpeaker() != null) {
            eventOutDto.setSpeakerId(newEvent.getSpeaker().getId());
        }

        return eventOutDto;
    }

    // DELETE
    public void delete(long id) throws EventNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        eventRepository.delete(event);
    }

    // GET ALL (con filtros)
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

        List<EventOutDto> eventOutDtoList =
                modelMapper.map(events, new TypeToken<List<EventOutDto>>() {}.getType());

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speaker salga a 0
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getSpeaker() != null) {
                eventOutDtoList.get(i).setSpeakerId(events.get(i).getSpeaker().getId());
            }
        }

        return eventOutDtoList;
    }

    // GET BY ID
    public EventOutDto findById(long id) throws EventNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        EventOutDto eventOutDto = modelMapper.map(event, EventOutDto.class);

        // Evita que speakerId salga a 0
        if (event.getSpeaker() != null) {
            eventOutDto.setSpeakerId(event.getSpeaker().getId());
        }

        return eventOutDto;
    }

    // PUT
    public EventOutDto modify(long id, EventInDto eventInDto) throws EventNotFoundException, SpeakerNotFoundException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);
        Speaker speaker = speakerRepository.findById(eventInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        modelMapper.map(eventInDto, existingEvent);
        existingEvent.setId(id);
        existingEvent.setSpeaker(speaker);

        Event updateEvent = eventRepository.save(existingEvent);
        EventOutDto updatedEventOutDto = modelMapper.map(updateEvent, EventOutDto.class);

        if (updateEvent.getSpeaker() != null) {
            updatedEventOutDto.setSpeakerId(updateEvent.getSpeaker().getId());
        }
        return updatedEventOutDto;
    }
}
