package com.creatorhub.platform.calendar.adapter.out.persistence;

import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataCalendarRepository extends JpaRepository<CalendarEntity, CalendarId> {
    List<CalendarEntity> findByOwnerId(UserId ownerId);
}