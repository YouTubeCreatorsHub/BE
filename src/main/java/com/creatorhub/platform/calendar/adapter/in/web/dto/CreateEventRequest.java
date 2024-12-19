package com.creatorhub.platform.calendar.adapter.in.web.dto;

import java.time.LocalDateTime;

public record CreateEventRequest(
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}