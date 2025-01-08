package com.creatorhub.platform.calendar.application;

import com.creatorhub.platform.calendar.application.event.*;
import com.creatorhub.platform.calendar.application.port.in.command.*;
import com.creatorhub.platform.calendar.application.port.out.CalendarRepository;
import com.creatorhub.platform.calendar.common.execption.AccessDeniedException;
import com.creatorhub.platform.calendar.common.execption.EventNotFoundException;
import com.creatorhub.platform.calendar.domain.entity.Calendar;
import com.creatorhub.platform.calendar.domain.entity.CalendarEvent;
import com.creatorhub.platform.calendar.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CalendarService calendarService;

    private UserId ownerId;
    private UserId otherUserId;
    private CalendarId calendarId;
    private Calendar calendar;

    @BeforeEach
    void setUp() {
        ownerId = UserId.newId();
        otherUserId = UserId.newId();
        calendarId = CalendarId.newId();
        calendar = Calendar.create(ownerId, new CalendarTitle("Test Calendar"));
    }

    @Nested
    @DisplayName("Calendar 관련 테스트")
    class CalendarTest {
        @Test
        @DisplayName("Calendar를 생성할 수 있다")
        void canCreateCalendar() {
            // Given
            CreateCalendarCommand command = new CreateCalendarCommand(new CalendarTitle("New Calendar"));
            given(calendarRepository.save(any(Calendar.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            Calendar createdCalendar = calendarService.cerateCalendar(command, ownerId);

            // Then
            assertThat(createdCalendar.getTitle()).isEqualTo(command.title());
            assertThat(createdCalendar.getOwnerId()).isEqualTo(ownerId);
            verify(calendarRepository).save(any(Calendar.class));
        }

        @Test
        @DisplayName("Calendar를 조회할 수 있다")
        void canGetCalendar() {
            // Given
            GetCalendarCommand command = new GetCalendarCommand(calendarId);
            given(calendarRepository.findById(calendarId)).willReturn(calendar);

            // When
            Calendar fetchedCalendar = calendarService.getCalendar(command, ownerId);

            // Then
            assertThat(fetchedCalendar).isEqualTo(calendar);
        }

        @Test
        @DisplayName("소유자의 모든 Calendar를 조회할 수 있다")
        void canGetAllCalendarsByOwner() {
            // Given
            List<Calendar> expectedCalendars = Collections.singletonList(calendar);
            given(calendarRepository.findByOwnerId(ownerId))
                    .willReturn(expectedCalendars);

            // When
            List<Calendar> fetchedCalendars = calendarService.getAllCalendar(ownerId);

            // Then
            assertThat(fetchedCalendars).isEqualTo(expectedCalendars);
        }

        @Test
        void canUpdateCalendarTitle() {
            // Given
            CalendarTitle newTitle = new CalendarTitle("Updated Title");
            UpdateCalendarCommand command = new UpdateCalendarCommand(calendarId, newTitle);
            given(calendarRepository.findById(calendarId)).willReturn(calendar);
            given(calendarRepository.save(any(Calendar.class))).willReturn(calendar);

            // When
            Calendar updatedCalendar = calendarService.updateCalendar(command, ownerId);

            // Then
            assertThat(updatedCalendar.getTitle()).isEqualTo(newTitle);
            verify(calendarRepository).findById(calendarId);
            verify(calendarRepository).save(any(Calendar.class));
            verify(eventPublisher).publishEvent(any(CalendarUpdated.class));
        }
    }

    @Nested
    @DisplayName("CalendarEvent 관련 테스트")
    class CalendarEventTest {
        private CalendarEvent event;

        @BeforeEach
        void setUp() {
            event = calendar.addEvent(
                    new EventTitle("Test Event"),
                    new EventDescription("Test Description"),
                    new DateTimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
                    ownerId
            );
        }

        @Test
        @DisplayName("Calendar에 Event를 추가할 수 있다")
        void canCreateEventInCalendar() {
            // Given
            EventTitle eventTitle = new EventTitle("New Event");
            EventDescription eventDescription = new EventDescription("New Description");
            DateTimeRange timeRange = new DateTimeRange(
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(2)
            );
            CreateCalendarEventCommand command = new CreateCalendarEventCommand(
                    calendarId,
                    eventTitle,
                    eventDescription,
                    timeRange
            );
            given(calendarRepository.findById(calendarId)).willReturn(calendar);
            given(calendarRepository.save(any(Calendar.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            CalendarEvent createdEvent = calendarService.createEvent(command, ownerId);

            // Then
            assertThat(createdEvent.getTitle()).isEqualTo(eventTitle);
            assertThat(createdEvent.getDescription()).isEqualTo(eventDescription);
            assertThat(createdEvent.getTimeRange()).isEqualTo(timeRange);
            verify(calendarRepository).save(calendar);
            verify(eventPublisher).publishEvent(any(CalendarEventCreated.class));
        }

        @Test
        @DisplayName("다른 사용자가 Calendar의 Event를 조회하려고 하면 예외가 발생한다")
        void throwsExceptionWhenOtherUserTriesToGetEvents() {
            // Given
            GetAllEventCommand command = new GetAllEventCommand(calendarId);
            given(calendarRepository.findById(calendarId)).willReturn(calendar);

            // Then
            assertThatThrownBy(() -> calendarService.getAllEvent(command, otherUserId))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("does not have permission");
        }

        @Test
        @DisplayName("존재하지 않는 Event를 조회하려고 하면 예외가 발생한다")
        void throwsExceptionWhenEventNotFound() {
            // Given
            CalendarEventId nonExistentEventId = CalendarEventId.newId();
            GetEventCommand command = new GetEventCommand(calendarId, nonExistentEventId);
            given(calendarRepository.findById(calendarId)).willReturn(calendar);

            // Then
            assertThatThrownBy(() -> calendarService.getEvent(command, ownerId))
                    .isInstanceOf(EventNotFoundException.class);
        }
    }
}