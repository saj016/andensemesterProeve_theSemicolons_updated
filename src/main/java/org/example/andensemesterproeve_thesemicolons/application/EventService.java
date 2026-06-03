package org.example.andensemesterproeve_thesemicolons.application;

import org.example.andensemesterproeve_thesemicolons.domain.Event;
import org.example.andensemesterproeve_thesemicolons.domain.enums.EventStatus_ENUM;
import org.example.andensemesterproeve_thesemicolons.domain.interfacesRepo.IEventRepository;
import org.example.andensemesterproeve_thesemicolons.exceptions.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final IEventRepository eventRepository;
    private final ExceptionService exceptionService;

    public EventService(IEventRepository eventRepository, ExceptionService exceptionService) {
        this.eventRepository = eventRepository;
        this.exceptionService = exceptionService;
    }

    public List<Event> getAllEvents() {
        try {
            return eventRepository.findAllEvents();
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public boolean signUpForEvent(int userId, int eventId) {
        try {
            int currentNumberOfParticipants = eventRepository.getNumberOfParticipantsFromId(eventId);
            Event event = getEventById(eventId);

            if (event.getEventStatus() != EventStatus_ENUM.Aaben_for_tilmelding) {
                return false;
            }

            if (currentNumberOfParticipants >= event.getMaxPlayers()) {
                return false;
            }
            if (!eventRepository.userIsAlreadySignedUp(userId, eventId)) {
                eventRepository.signUserUpForEvent(userId, eventId);
                int newNumberOfParticipants = currentNumberOfParticipants + 1;
                if (newNumberOfParticipants >= event.getMaxPlayers()) {
                    eventRepository.updateEventStatus(eventId, EventStatus_ENUM.Fuldt_booket.name());
                }
                return true;
            }
            return true;
        } catch (EmptyResultDataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public Event getEventById(int eventId) {
        try {
            Event event = eventRepository.getEventById(eventId);
            event.setParticipants(eventRepository.getEventParticipantsBasicInfoOnlyByEventId(eventId));
            return event;
        } catch (EmptyResultDataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public List<Event> getAllMyArrangedEvents(int userId) {
        try {
            return eventRepository.findAllMyArrangedEvents(userId);
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public List<Event> getAllMySignedUpEvents(int userId) {
        try {
            return eventRepository.findAllMySignedUpEvents(userId);
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public void cancelRegistrationForEvent(int userId, int eventId) {
        try {
            eventRepository.cancelRegistrationToEvent(userId, eventId);

            Event event = eventRepository.getEventById(eventId);
            int currentNumberOfParticipants = eventRepository.getNumberOfParticipantsFromId(eventId);
            if (currentNumberOfParticipants <= event.getMaxPlayers()) {
                eventRepository.updateEventStatus(eventId, EventStatus_ENUM.Aaben_for_tilmelding.name());
            }
        } catch (EmptyResultDataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public void createNewEvent(Event event) {
        try {
            eventRepository.createEvent(event);
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public void updateEvent(Event event) {
        try {
            eventRepository.updateEventInfo(event);
        } catch (DataAccessException e) {
            exceptionService.logException(e);
            throw e;
        } catch (Exception e) {
            exceptionService.logException(e);
            throw e;
        }
    }

    public List<Event> getAllMyArrangedEventsFiltered(List<Event> events, String open, String concluded){
        if ((open == null || open.isEmpty()) && (concluded == null || concluded.isEmpty())){
            return events;
        }
        List<Event> filteredMyArrangedEventsList = new ArrayList<>();
        for (Event event : events){
            boolean matchOpenForSignUp = (open !=null && open.equals("true") && !event.getEventStatus().equals(EventStatus_ENUM.Afholdt));
            boolean matchConcluded = (concluded !=null && concluded.equals("true") && event.getEventStatus().equals(EventStatus_ENUM.Afholdt));

            if (matchOpenForSignUp || matchConcluded){
                filteredMyArrangedEventsList.add(event);
            }
        } return filteredMyArrangedEventsList;
    }

    public List<Event> getAllEventsSorted(String sortBy) {
        List<Event> events = getAllEvents();
       sortEventList(sortBy, events);
        return events;
    }

    public List<Event> getAllMyArrangedEventsSorted(String sortBy, int userId){
        List<Event> events = getAllMyArrangedEvents(userId);
        sortEventList(sortBy, events);
        return events;
    }

    public List<Event> getAllMySignedUpEventsSorted(String sortBy, int userId){
        List<Event> events = getAllMySignedUpEvents(userId);
        sortEventList(sortBy, events);
        return events;
    }

    public void sortEventList(String sortBy, List<Event> events){
        if (sortBy != null && !sortBy.isEmpty()) {
            if (sortBy.equals("date_desc")) {
                sortByDate(events);
            } else if (sortBy.equals("openForSignUp")) {
                sortByOpenForSignUp(events);
            } else if (sortBy.equals("casual")) {
                sortByCasual(events);
            } else if (sortBy.equals("tournament")) {
                sortByTournament(events);

            }
        }
    }

    private void sortByDate(List<Event> events) {
        events.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
    }

    private void sortByOpenForSignUp(List<Event> events) {
        events.sort((e1, e2)-> e1.getEventStatus().compareTo(e2.getEventStatus()));
    }
    private void sortByCasual(List<Event> events) {
        events.sort((e1, e2) -> e1.getType().compareTo(e2.getType())); //Sorting by 1st enum on the list (casual)
    }

    private void sortByTournament(List<Event> events) {
        events.sort((e1, e2)-> e2.getType().compareTo(e1.getType())); //Sorting by 2nd enum on the list (turnering)
    }
}