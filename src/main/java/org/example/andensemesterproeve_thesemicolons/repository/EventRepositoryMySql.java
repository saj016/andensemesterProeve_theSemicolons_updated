package org.example.andensemesterproeve_thesemicolons.repository;

import org.example.andensemesterproeve_thesemicolons.domain.*;
import org.example.andensemesterproeve_thesemicolons.domain.enums.EventStatus_ENUM;
import org.example.andensemesterproeve_thesemicolons.domain.enums.EventType_ENUM;
import org.example.andensemesterproeve_thesemicolons.domain.interfacesRepo.IEventRepository;
import org.example.andensemesterproeve_thesemicolons.exceptions.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class EventRepositoryMySql implements IEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventRepositoryMySql(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> findAllEvents() {
        String sql = """
                SELECT events.*, users.username
                            FROM events
                            JOIN users ON events.creator_id = users.id
                """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                User eventCreator = new User();
                eventCreator.setId(rs.getInt("creator_id"));
                eventCreator.setUsername("username");


                return new Event(
                        rs.getInt("id"),
                        rs.getString("name"),
                        eventCreator,
                        EventType_ENUM.valueOf(rs.getString("event_type")),
                        rs.getString("format"),
                        rs.getInt("max_players"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getTime("event_time").toLocalTime(),
                        EventStatus_ENUM.valueOf(rs.getString("event_status"))
                );
            });

        } catch (Exception e) {
            throw new DataAccessException("Error in findAllEvents()", e);
        }
    }

    @Override
    public void signUserUpForEvent(int userId, int eventId) {
        String sql = """
                INSERT INTO event_users (event_id, user_id) VALUES(?,?)
                """;

        try {
            jdbcTemplate.update(sql, eventId, userId);
        } catch (Exception e) {
            throw new DataAccessException("Error in signUserUpForEvent()", e);
        }
    }

    @Override
    public boolean userIsAlreadySignedUp(int userId, int eventId) throws EmptyResultDataAccessException {
        String sql= """
                SELECT COUNT(*) FROM event_users WHERE event_id= ? AND user_id= ?
                """;
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventId, userId); //vi bruger Integer fremfor int, fordi vi kan få returneret null
            return count != null && count > 0;
        } catch (Exception e) {
            throw new DataAccessException("Error in userIsAlreadySignedUp()", e);
        }
    }

    @Override
    public List<Event> findAllMyArrangedEvents(int userId) {
        String sql = """
                SELECT * FROM events
                WHERE creator_id = ?
                """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new Event(
                            rs.getInt("id"),
                            rs.getString("name"),
                            EventType_ENUM.valueOf(rs.getString("event_type")),
                            rs.getString("format"),
                            rs.getInt("max_players"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getTime("event_time").toLocalTime(),
                            EventStatus_ENUM.valueOf(rs.getString("event_status"))
                    ), userId
            );
        } catch (Exception e) {
            throw new DataAccessException("Error in findAllMyArrangedEvents()", e);
        }
    }

    @Override
    public List<Event> findAllMySignedUpEvents(int userId) {
        String sql = """
                SELECT events.* FROM events
                                INNER JOIN event_users ON events.id = event_users.event_id
                                WHERE event_users.user_id = ?
                """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new Event(
                            rs.getInt("id"),
                            rs.getString("name"),
                            EventType_ENUM.valueOf(rs.getString("event_type")),
                            rs.getString("format"),
                            rs.getInt("max_players"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getTime("event_time").toLocalTime(),
                            EventStatus_ENUM.valueOf(rs.getString("event_status"))
                    ), userId
            );
        } catch (Exception e) {
            throw new DataAccessException("Error in findAllMySignedUpEvents()", e);
        }
    }

    @Override
    public void cancelRegistrationToEvent(int userId, int eventId) {
        String sql = """
                DELETE FROM event_users WHERE user_id=? AND event_id=?
                """;

        try {
            jdbcTemplate.update(sql, userId, eventId);
        } catch (Exception e) {
            throw new DataAccessException("Error in cancelRegistrationToEvent()", e);
        }
    }

    @Override
    public void createEvent(Event event) {
       String sql = """
                INSERT INTO events (creator_id, name, event_type,format, max_players, event_date, event_time, event_status)
                VALUES (?,?,?,?,?,?,?,'Aaben_for_tilmelding' );
        """;

       try {
           jdbcTemplate.update(sql, event.getCreator().getId(), event.getName(), event.getType().name(), event.getFormat(), event.getMaxPlayers(), event.getDate(), event.getTime());
       } catch (Exception e) {
           throw new DataAccessException("Error in createEvent()", e);
       }
    }

    @Override
    public Event getEventById(int eventId) throws EmptyResultDataAccessException {

        String sql = """
                SELECT * FROM events
                                WHERE id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                User eventCreator = new User();
                eventCreator.setId(rs.getInt("creator_id"));

                return new Event(
                        rs.getInt("id"),
                        rs.getString("name"),
                        eventCreator,
                        EventType_ENUM.valueOf(rs.getString("event_type")),
                        rs.getString("format"),
                        rs.getInt("max_players"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getTime("event_time").toLocalTime(),
                        EventStatus_ENUM.valueOf(rs.getString("event_status"))
                );
            }, eventId);
        } catch (Exception e) {
            throw new DataAccessException("Error in getEventById()", e);
        }
    }

    @Override
    public List<User> getEventParticipantsBasicInfoOnlyByEventId(int eventId) {
        try {
            String sql = """
                SELECT users.id, username FROM users
                JOIN event_users ON users.id = event_users.user_id
                WHERE event_id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getInt("users.id"),
                        rs.getString("username")
                        ), eventId
        );
        } catch (Exception e) {
            throw new DataAccessException("Error in getEventParticipantsBasicInfoOnlyByEventId()", e);
        }
    }


    @Override
    public int getNumberOfParticipantsFromId(int eventId) throws EmptyResultDataAccessException {
        String sql= """
                SELECT COUNT(*) FROM event_users WHERE event_id= ?
                """;

        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventId);
            if (count == null) {
                return 0;
            }
            return count;
        } catch (Exception e) {
            throw new DataAccessException("Error in getNumberOfParticipantsFromId()", e);
        }
    }

    @Override
    public void updateEventStatus(int eventId, String newStatus) {
        String sql = """
                UPDATE events
                SET event_status = ?
                WHERE id = ?
                """;

        try {
            jdbcTemplate.update(sql, newStatus, eventId);
        } catch (Exception e) {
            throw new DataAccessException("Error in updateEventStatus()", e);
        }
    }

    @Override
    public void updateStatusForConcludedEvents() {
        String sql = """
                UPDATE events
                SET event_status = 'Afholdt'
                WHERE event_date < CURDATE()
                AND event_status != 'Afholdt'
                """;

        try {
            jdbcTemplate.update(sql);
        } catch (Exception e) {
            throw new DataAccessException("Error in updateStatusForConcludedEvents()", e);
        }
    }

    @Override
    public void updateStatusForOngoingEvents() {
        String sql = """
                UPDATE events
                SET event_status = 'Lukket_for_tilmelding'
                WHERE event_date <= DATE_ADD(CURDATE(), INTERVAL 1 DAY)
                AND event_status != 'Lukket_for_tilmelding'
                AND event_status != 'Afholdt'
                """;

        try {
            jdbcTemplate.update(sql);
        } catch (Exception e) {
            throw new DataAccessException("Error in updateStatusForOngoingEvents()", e);
        }

    }

    @Override
    public void updateEventInfo(Event event) {
        String sql = """
                UPDATE events
                SET name = ?,
                event_type = ?,
                format = ?,
                max_players = ?,
                event_date = ?,
                event_time = ?
                WHERE id=?
                """;

        try {
            jdbcTemplate.update(sql,
                    event.getName(),
                    event.getType().name(),
                    event.getFormat(),
                    event.getMaxPlayers(),
                    event.getDate(),
                    event.getTime(),
                    event.getId()
            );
        } catch (Exception e) {
            throw new DataAccessException("Error in updateEventInfo()", e);
        }
    }

    @Override
    public void registerWinnerOfEventByEventId(int winnerId, int eventId) {
        try {
            String sql = """
                    UPDATE event_users
                    SET leaderboard_placing = 1
                    WHERE event_id = ?
                    AND user_id = ?
                    """;

            jdbcTemplate.update(sql, eventId, winnerId);
        } catch (Exception e) {
            throw new DataAccessException("Error in registerWinnerOfEventByEventId()", e);
        }
    }

    @Override
    public void clearEventLeaderboard(int eventId) {
        try {
            String sql = """
                    UPDATE event_users
                    SET leaderboard_placing = null
                    WHERE event_id = ?
                    """;

            jdbcTemplate.update(sql, eventId);
        } catch (Exception e) {
            throw new DataAccessException("Error in clearEventLeaderboard()", e);
        }
    }
}
