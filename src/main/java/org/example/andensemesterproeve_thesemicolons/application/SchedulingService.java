package org.example.andensemesterproeve_thesemicolons.application;

import org.example.andensemesterproeve_thesemicolons.domain.interfacesRepo.IEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    @Autowired
    private EventService eventService;

    @Scheduled(initialDelay = 0, fixedRate = 300000) //initialDelay 0 makes sure it runs when the application is started, 300.000 milliseconds is 5 minutes
    public void updateEventsStatus(){
        eventService.updateAllEventStatus();
    }
}
