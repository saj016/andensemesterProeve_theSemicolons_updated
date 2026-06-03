package org.example.andensemesterproeve_thesemicolons.web;

import jakarta.servlet.http.HttpSession;
import org.example.andensemesterproeve_thesemicolons.application.EventService;
import org.example.andensemesterproeve_thesemicolons.domain.Event;
import org.example.andensemesterproeve_thesemicolons.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/myEvents")
    public String getMyEvents(@RequestParam(name = "sortBy", required = false) String sortBy, @RequestParam(name = "open", required = false) String open,
                              @RequestParam(name = "concluded", required = false) String concluded, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("sessionUser", user);
        List<Event> myArrangedEvents = eventService.getAllMyArrangedEventsSorted(sortBy, user.getId());
        List<Event> mySignedUpEvents = eventService.getAllMySignedUpEventsSorted(sortBy, user.getId());
        List<Event> myArrangedEventsFiltered = eventService.getAllMyArrangedEventsFiltered(myArrangedEvents, open, concluded);
        model.addAttribute("mySignedUpEvents", mySignedUpEvents);
        model.addAttribute("myArrangedEvents", myArrangedEvents);
        model.addAttribute("selectedSort", sortBy);
        model.addAttribute("myArrangedEvents", myArrangedEventsFiltered);
        model.addAttribute("openParam", open);
        model.addAttribute("concludedParam", concluded);
        return "/event/myEvents";
    }

    @GetMapping("/allEvents")
    public String getAllEvents(@RequestParam(name = "sortBy", required = false) String sortBy, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        List<Event> events = eventService.getAllEventsSorted(sortBy);

        model.addAttribute("events", events);
        model.addAttribute("selectedSort", sortBy);

        return "event/allEvents";
    }

    @GetMapping("/allEvents/{id}")
    public String getEventPage(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("event", eventService.getEventById(id));
        model.addAttribute("user", user);

        return "event/eventInfo";
    }

    @GetMapping("/signUp/myEvents/{eventId}")
    public String signUpToEvent(@PathVariable("eventId") int eventId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        int userId = user.getId();
        if (!eventService.signUpForEvent(userId, eventId)) {
            model.addAttribute("error", "Ikke muligt at tilmelde sig event");
            model.addAttribute("events", eventService.getAllEvents());
            return "event/allEvents";
        } else{
            return "redirect:/myEvents";
        }
    }

    @GetMapping("/cancelRegistration/myEvents/{eventId}")
    public String cancelRegistrationToEvent(@PathVariable("eventId") int eventId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        int userId = user.getId();
        eventService.cancelRegistrationForEvent(userId, eventId);
        return "redirect:/myEvents";
    }

    @GetMapping("/addEvent")
    public String addEvent(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("event", new Event());
        return "/event/addEvent";
    }

    @PostMapping("/addEvent")
    public String createEvent(@ModelAttribute Event event, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        event.setCreator(user);
        eventService.createNewEvent(event);
        return "redirect:/myEvents";
    }

    @GetMapping("/editEvent/myEvents/{eventId}")
    public String editEvent(@PathVariable("eventId") int eventId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        return ("event/editEvent");
    }

    @PostMapping("/updateEvent")
    public String updateEvent(@ModelAttribute Event event) {
        eventService.updateEvent(event);
        return ("redirect:/myEvents");
    }

    @PostMapping("/editEvent/setEventWinner")
    public String submitEventWinner(@RequestParam(name = "userId") int winnerId,
                                    @RequestParam(name = "eventId") int eventId) {

        if (winnerId == -1) {
            eventService.setEventToNoWinner(eventId);
        } else {
            eventService.updateWinnerOfEvent(winnerId, eventId);
        }
        return "redirect:/allEvents";
    }
}
