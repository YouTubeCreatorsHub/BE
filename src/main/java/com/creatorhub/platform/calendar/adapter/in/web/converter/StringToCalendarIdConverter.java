package com.creatorhub.platform.calendar.adapter.in.web.converter;

import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StringToCalendarIdConverter implements Converter<String, CalendarId> {
    @Override
    public CalendarId convert(String source) {
        return new CalendarId(UUID.fromString(source));
    }
}