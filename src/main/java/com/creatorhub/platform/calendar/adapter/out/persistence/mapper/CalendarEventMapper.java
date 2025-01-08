package com.creatorhub.platform.calendar.adapter.out.persistence.mapper;

import com.creatorhub.platform.calendar.adapter.out.persistence.CalendarEntity;
import com.creatorhub.platform.calendar.adapter.out.persistence.CalendarEventEntity;
import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;

class CalendarEventMapper {

    public static CalendarEventEntity toEntity(CalendarEvent domain, CalendarEntity calendar) {
        CalendarEventEntity entity = CalendarEventEntity.builder()
                .id(domain.getId())
                .calendar(calendar)
                .title(domain.getTitle())
                .description(domain.getDescription())
                .timeRange(domain.getTimeRange())
                .status(domain.getStatus())
                .build();
        return entity;
    }

    public static CalendarEvent toDomain(CalendarEventEntity entity) {
        return CalendarEvent.create(
                entity.getTitle(),
                entity.getDescription(),
                entity.getTimeRange()
        );
    }
}