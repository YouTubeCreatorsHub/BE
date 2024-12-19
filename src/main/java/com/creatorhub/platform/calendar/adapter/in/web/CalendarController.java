package com.creatorhub.platform.calendar.adapter.in.web;

import com.creatorhub.platform.calendar.adapter.in.web.dto.*;
import com.creatorhub.platform.calendar.application.CalendarService;
import com.creatorhub.platform.calendar.application.port.in.command.*;
import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    // Calendar 관련 엔드포인트
    @GetMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> getCalendar(
            @PathVariable CalendarId calendarId,
            @RequestHeader("X-User-Id") String userId) {
        GetCalendarCommand command = new GetCalendarCommand(calendarId);
        Calendar calendar = calendarService.getCalendar(command, UserId.of(userId));
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }

    @GetMapping
    public ResponseEntity<List<CalendarResponse>> getAllCalendars(
            @RequestHeader("X-User-Id") String userId) {
        List<Calendar> calendars = calendarService.getAllCalendar(UserId.of(userId));
        List<CalendarResponse> responses = calendars.stream()
                .map(CalendarResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CalendarResponse> createCalendar(
            @RequestBody CreateCalendarRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        CreateCalendarCommand command = new CreateCalendarCommand(
                new CalendarTitle(request.title())
        );
        Calendar calendar = calendarService.cerateCalendar(command, UserId.of(userId));
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }

    @PatchMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> updateCalendar(
            @PathVariable CalendarId calendarId,
            @RequestBody String newTitle,
            @RequestHeader("X-User-Id") String userId
    ) {
        UpdateCalendarCommand command = new UpdateCalendarCommand(calendarId, new CalendarTitle(newTitle));
        Calendar calendar = calendarService.updateCalendar(command, UserId.of(userId));
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }

    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(
            @PathVariable CalendarId calendarId,
            @RequestHeader("X-User-Id") String userId
    ) {
        DeleteCalendarCommand command = new DeleteCalendarCommand(calendarId);
        calendarService.deleteCalendar(command, UserId.of(userId));
        return ResponseEntity.ok().build();
    }

    // Calendar Event 관련 엔드포인트
    @GetMapping("/{calendarId}/events")
    public ResponseEntity<List<CalendarEventResponse>> getEvents(
            @PathVariable CalendarId calendarId,
            @RequestHeader("X-User-Id") String userId) {
        List<CalendarEvent> events = calendarService.getAllEvent(
                new GetAllEventCommand(calendarId),
                UserId.of(userId)
        );
        List<CalendarEventResponse> eventResponses = events.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventResponses);
    }

    @GetMapping("/{calendarId}/events/{eventId}")
    public ResponseEntity<CalendarEventResponse> getEvent(
            @PathVariable CalendarId calendarId,
            @PathVariable CalendarEventId eventId,
            @RequestHeader("X-User-Id") String userId) {
        GetEventCommand command = new GetEventCommand(calendarId, eventId);
        CalendarEvent event = calendarService.getEvent(command, UserId.of(userId));
        return ResponseEntity.ok(CalendarEventResponse.from(event));
    }

    @PostMapping("/{calendarId}/events")
    public ResponseEntity<CalendarEventResponse> createEvent(
            @PathVariable CalendarId calendarId,
            @RequestBody CreateEventRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CreateCalendarEventCommand command = new CreateCalendarEventCommand(
                calendarId,
                new EventTitle(request.title()),
                new EventDescription(request.description()),
                new DateTimeRange(request.startTime(), request.endTime())
        );
        CalendarEvent event = calendarService.createEvent(command, UserId.of(userId));
        return ResponseEntity.ok(CalendarEventResponse.from(event));
    }

    @PutMapping("/{calendarId}/events/{eventId}")
    public ResponseEntity<CalendarEventResponse> updateEvent(
            @PathVariable CalendarId calendarId,
            @PathVariable CalendarEventId eventId,
            @RequestBody UpdateEventRequest request,
            @RequestHeader("X-User-Id") String userId) {
        UpdateCalendarEventCommand command = new UpdateCalendarEventCommand(
                calendarId,
                eventId,
                new EventTitle(request.title()),
                new EventDescription(request.description()),
                new DateTimeRange(request.startTime(), request.endTime())
        );
        CalendarEvent event = calendarService.updateEvent(command, UserId.of(userId));
        return ResponseEntity.ok(CalendarEventResponse.from(event));
    }

    @DeleteMapping("/{calendarId}/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable CalendarId calendarId,
            @PathVariable CalendarEventId eventId,
            @RequestHeader("X-User-Id") String userId) {
        DeleteCalendarEventCommand command = new DeleteCalendarEventCommand(calendarId, eventId);
        calendarService.deleteEvent(command, UserId.of(userId));
        return ResponseEntity.ok().build();
    }
}