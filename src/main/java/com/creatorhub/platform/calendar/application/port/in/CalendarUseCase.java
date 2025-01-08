package com.creatorhub.platform.calendar.application.port.in;

import com.creatorhub.platform.calendar.application.port.in.command.CreateCalendarCommand;
import com.creatorhub.platform.calendar.application.port.in.command.DeleteCalendarCommand;
import com.creatorhub.platform.calendar.application.port.in.command.GetCalendarCommand;
import com.creatorhub.platform.calendar.application.port.in.command.UpdateCalendarCommand;
import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.vo.UserId;

import java.util.List;

public interface CalendarUseCase {
    Calendar cerateCalendar(CreateCalendarCommand command, UserId userId);
    Calendar getCalendar(GetCalendarCommand command, UserId userId);
    List<Calendar> getAllCalendar(UserId userId);
    Calendar updateCalendar(UpdateCalendarCommand command, UserId userId);
    void deleteCalendar(DeleteCalendarCommand command, UserId userId);
}
