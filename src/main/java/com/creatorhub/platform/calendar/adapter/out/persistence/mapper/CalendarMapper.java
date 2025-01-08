package com.creatorhub.platform.calendar.adapter.out.persistence.mapper;

import com.creatorhub.platform.calendar.adapter.out.persistence.CalendarEntity;
import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.vo.CalendarPermission;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CalendarMapper {
    public static CalendarEntity toEntity(Calendar domain) {
        CalendarEntity entity = CalendarEntity.builder()
                .id(domain.getId())
                .ownerId(domain.getOwnerId())
                .title(domain.getTitle())
                .events(new HashSet<>())
                .permissions(new HashSet<>())
                .build();

        entity.getEvents().addAll(
                domain.getEvents().stream()
                        .map(event -> CalendarEventMapper.toEntity(event, entity))
                        .collect(Collectors.toSet())
        );

        entity.getPermissions().addAll(
                domain.getPermissions().stream()
                        .map(CalendarPermissionMapper::toEntity)
                        .collect(Collectors.toSet())
        );

        return entity;
    }

    public static Calendar toDomain(CalendarEntity entity) {
        Set<CalendarEvent> events = entity.getEvents().stream()
                .map(CalendarEventMapper::toDomain)
                .collect(Collectors.toSet());

        Set<CalendarPermission> permissions = entity.getPermissions().stream()
                .map(CalendarPermissionMapper::toDomain)
                .collect(Collectors.toSet());

        return Calendar.reconstruct(
                entity.getId(),
                entity.getOwnerId(),
                entity.getTitle(),
                events,
                permissions
        );
    }
}