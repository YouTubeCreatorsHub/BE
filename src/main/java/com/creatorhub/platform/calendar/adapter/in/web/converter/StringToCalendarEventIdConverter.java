package com.creatorhub.platform.calendar.adapter.in.web.converter;

import com.creatorhub.platform.calendar.domain.vo.CalendarEventId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StringToCalendarEventIdConverter implements Converter<String, CalendarEventId> {
    @Override
    public CalendarEventId convert(String source) {
        return new CalendarEventId(UUID.fromString(source));
    }
}