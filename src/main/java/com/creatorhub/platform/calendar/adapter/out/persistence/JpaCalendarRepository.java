package com.creatorhub.platform.calendar.adapter.out.persistence;

import com.creatorhub.platform.calendar.application.port.out.CalendarRepository;
import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.adapter.out.persistence.mapper.CalendarMapper;
import com.creatorhub.platform.calendar.domain.vo.UserId;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JpaCalendarRepository implements CalendarRepository {
    private final SpringDataCalendarRepository calendarRepository;

    @Override
    public Calendar findById(CalendarId id) {
        CalendarEntity entity = calendarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Calendar not found with id: " + id.getValue()));
        return CalendarMapper.toDomain(entity);
    }

    @Override
    public Calendar save(Calendar calendar) {
        CalendarEntity entity = CalendarMapper.toEntity(calendar);
        entity = calendarRepository.save(entity);
        return CalendarMapper.toDomain(entity);
    }

    @Override
    public List<Calendar> findByOwnerId(UserId ownerId) {
        List<CalendarEntity> entities = calendarRepository.findByOwnerId(ownerId);
        return entities.stream()
                .map(CalendarMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(CalendarId calendarId) {
        calendarRepository.deleteById(calendarId);
    }
}