package com.creatorhub.platform.calendar.application.port.out;

import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.UserId;

import java.util.List;

public interface CalendarRepository {
    Calendar findById(CalendarId id);

    Calendar save(Calendar calendar);
    List<Calendar> findByOwnerId(UserId ownerId);
    void deleteById(CalendarId calendarId);
}
