package com.creatorhub.platform.calendar.adapter.out.persistence.mapper;

import com.creatorhub.platform.calendar.adapter.out.persistence.CalendarEntity;
import com.creatorhub.platform.calendar.adapter.out.persistence.CalendarPermissionEntity;
import com.creatorhub.platform.calendar.domain.vo.CalendarPermission;

import java.util.Set;
import java.util.stream.Collectors;

public class CalendarPermissionMapper {

    public static CalendarPermissionEntity toEntity(CalendarPermission domain) {
        return CalendarPermissionEntity.builder()
                .userId(domain.getUserId())
                .level(domain.getLevel())
                .build();
    }

    public static CalendarPermission toDomain(CalendarPermissionEntity entity) {
        return new CalendarPermission(
                entity.getUserId(),
                entity.getLevel()
        );
    }

    public static Set<CalendarPermission> toDomainSet(Set<CalendarPermissionEntity> entities) {
        return entities.stream()
                .map(CalendarPermissionMapper::toDomain)
                .collect(Collectors.toSet());
    }

    public static Set<CalendarPermissionEntity> toEntitySet(Set<CalendarPermission> domains, CalendarEntity calendar) {
        return domains.stream()
                .map(CalendarPermissionMapper::toEntity)
                .collect(Collectors.toSet());
    }
}