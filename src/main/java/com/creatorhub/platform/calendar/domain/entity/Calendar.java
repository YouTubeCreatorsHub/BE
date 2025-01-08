package com.creatorhub.platform.calendar.domain.entity;

import com.creatorhub.platform.calendar.common.execption.AccessDeniedException;
import com.creatorhub.platform.calendar.common.execption.EventNotFoundException;
import com.creatorhub.platform.calendar.domain.vo.*;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Calendar {
    private final CalendarId id;
    private final UserId ownerId;
    private CalendarTitle title;
    private final Set<CalendarEvent> events;
    private final Set<CalendarPermission> permissions;

    private Calendar(CalendarId id, UserId ownerId, CalendarTitle title) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.events = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    public static Calendar create(UserId ownerId, CalendarTitle title) {
        return new Calendar(CalendarId.newId(), ownerId, title);
    }

    public static Calendar reconstruct(CalendarId id, UserId ownerId, CalendarTitle title,
                                       Set<CalendarEvent> events, Set<CalendarPermission> permissions) {
        Calendar calendar = new Calendar(id, ownerId, title);
        calendar.events.addAll(events);
        calendar.permissions.addAll(permissions);
        return calendar;
    }

    public CalendarEvent addEvent(EventTitle title, EventDescription description,
                                  DateTimeRange timeRange, UserId requesterId) {
        validateAccess(requesterId, PermissionLevel.WRITE);
        CalendarEvent event = CalendarEvent.create(title, description, timeRange);
        events.add(event);
        return event;
    }
    public Calendar updateTitle(CalendarTitle newTitle, UserId requesterId) {
        validateAccess(requesterId, PermissionLevel.WRITE);
        this.title = newTitle;
        return this;
    }
    public CalendarEvent updateEvent(CalendarEventId eventId, EventTitle title, EventDescription description,
                            DateTimeRange timeRange, UserId requesterId) {
        validateAccess(requesterId, PermissionLevel.WRITE);
        CalendarEvent event = findEvent(eventId);
        event.update(title, description, timeRange);
        return event;
    }

    public void deleteEvent(CalendarEventId eventId, UserId requesterId) {
        validateAccess(requesterId, PermissionLevel.WRITE);
        CalendarEvent event = findEvent(eventId);
        events.remove(event);
    }

    public void addPermission(UserId userId, PermissionLevel level, UserId requesterId) {
        validateAccess(requesterId, PermissionLevel.ADMIN);
        permissions.add(new CalendarPermission(userId, level));
    }

    public Set<CalendarEvent> getEvents() {
        return Collections.unmodifiableSet(events);
    }

    public Set<CalendarPermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    private CalendarEvent findEvent(CalendarEventId eventId) {
        return events.stream()
                .filter(event -> event.getId().equals(eventId))
                .findFirst()
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private void validateAccess(UserId requesterId, PermissionLevel requiredLevel) {
        if (ownerId.equals(requesterId)) {
            return;
        }

        boolean hasPermission = permissions.stream()
                .filter(permission -> permission.getUserId().equals(requesterId))
                .anyMatch(permission -> permission.getLevel().isAtLeast(requiredLevel));

        if (!hasPermission) {
            throw new AccessDeniedException("Insufficient permissions for user: " + requesterId.getValue());
        }
    }
}