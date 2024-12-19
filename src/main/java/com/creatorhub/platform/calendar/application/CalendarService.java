package com.creatorhub.platform.calendar.application;

import com.creatorhub.platform.calendar.application.event.*;
import com.creatorhub.platform.calendar.application.port.in.*;
import com.creatorhub.platform.calendar.application.port.in.command.*;
import com.creatorhub.platform.calendar.application.port.out.CalendarRepository;
import com.creatorhub.platform.calendar.common.execption.AccessDeniedException;
import com.creatorhub.platform.calendar.common.execption.EventNotFoundException;
import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService implements CalendarUseCase, CalendarEventUseCase {
    private final CalendarRepository calendarRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Calendar 관련 메서드
    @Override
    public Calendar cerateCalendar(CreateCalendarCommand command, UserId userId) {
        Calendar calendar = Calendar.create(userId, command.title());
        return calendarRepository.save(calendar);
    }

    @Override
    public Calendar getCalendar(GetCalendarCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());
        return calendar;
    }

    @Override
    public List<Calendar> getAllCalendar(UserId userId) {
        List<Calendar> calendarList = calendarRepository.findByOwnerId(userId);
        return calendarList;
    }

    @Override
    public Calendar updateCalendar(UpdateCalendarCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());
        calendar.updateTitle(command.newTitle(), userId);
        Calendar savedCalendar = calendarRepository.save(calendar);
        eventPublisher.publishEvent(new CalendarUpdated(command.calendarId(), calendar.getTitle()));
        return savedCalendar;
    }

    @Override
    public void deleteCalendar(DeleteCalendarCommand command, UserId userId) {
        eventPublisher.publishEvent(new CalendarDeleted(command.calendarId()));
        calendarRepository.deleteById(command.calendarId());
    }

    // Calendar Event 관련 메서드
    @Override
    public List<CalendarEvent> getAllEvent(GetAllEventCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());

        if (!calendar.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("User " + userId.getValue() + " does not have permission to access calendar " + command.calendarId().getValue());
        }
        return new ArrayList<>(calendar.getEvents());
    }

    @Override
    @Transactional
    public CalendarEvent createEvent(CreateCalendarEventCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());
        CalendarEvent event = calendar.addEvent(
                command.title(),
                command.description(),
                command.timeRange(),
                userId
        );
        calendarRepository.save(calendar);
        eventPublisher.publishEvent(new CalendarEventCreated(command.calendarId(), event));
        return event;
    }

    @Override
    public CalendarEvent getEvent(GetEventCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());
        Set<CalendarEvent> events = calendar.getEvents();
        return events.stream()
                .filter(event -> event.getId().equals(command.calendarEventId()))
                .findFirst()
                .orElseThrow(() -> new EventNotFoundException(command.calendarEventId()));
    }

    @Override
    public CalendarEvent updateEvent(UpdateCalendarEventCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());
        CalendarEvent updatedEvent = calendar.updateEvent(
                command.eventId(),
                command.title(),
                command.description(),
                command.timeRange(),
                userId
        );
        calendarRepository.save(calendar);
        eventPublisher.publishEvent(new CalendarEventUpdated(command.calendarId(), updatedEvent));
        return updatedEvent;
    }

    @Override
    public void deleteEvent(DeleteCalendarEventCommand command, UserId userId) {
        Calendar calendar = calendarRepository.findById(command.calendarId());
        calendar.deleteEvent(command.eventId(), userId);
        eventPublisher.publishEvent(new CalendarEventDeleted(command.calendarId(), command.eventId()));
        calendarRepository.save(calendar);
    }
}