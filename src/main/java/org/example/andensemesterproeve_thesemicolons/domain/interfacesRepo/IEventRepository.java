package org.example.andensemesterproeve_thesemicolons.domain.interfacesRepo;

import org.example.andensemesterproeve_thesemicolons.domain.Event;
import org.example.andensemesterproeve_thesemicolons.domain.User;

import java.util.List;

public interface IEventRepository {
    List<Event> findAllEvents();

    void signUserUpForEvent(int userId, int eventId);

    boolean userIsAlreadySignedUp(int userId, int eventId);

    Event getEventById(int eventId);

    List<User> getEventParticipantsBasicInfoOnlyByEventId(int eventId);

    int getNumberOfParticipantsFromId(int eventId);

    void updateEventStatus(int eventId, String newStatus);

    void updateStatusForConcludedEvents();

    void updateStatusForOngoingEvents();

    void updateStatusForFullyBookedEvents();

    void reopenModifiedDateEvents();

    void reopenModifiedMaxPlayersEvents();

    List<Event> findAllMyArrangedEvents(int userId);

    List<Event> findAllMySignedUpEvents(int userId);

    void cancelRegistrationToEvent(int userId, int eventId);

    void createEvent(Event event);

    void updateEventInfo(Event event);

    void registerWinnerOfEventByEventId(int winnerId, int eventId);

    void clearEventLeaderboard(int eventId);
}
