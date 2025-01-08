package com.creatorhub.platform.calendar.domain.entity;

import com.creatorhub.platform.calendar.common.execption.AccessDeniedException;
import com.creatorhub.platform.calendar.common.execption.EventNotFoundException;
import com.creatorhub.platform.calendar.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CalendarTest {

    @Nested
    @DisplayName("캘린더 생성 테스트")
    class CalendarCreationTest {
        @Test
        @DisplayName("새로운 캘린더를 생성할 수 있다")
        void createNewCalendar() {
            // Given
            UserId ownerId = UserId.newId();
            CalendarTitle title = new CalendarTitle("My Calendar");

            // When
            Calendar calendar = Calendar.create(ownerId, title);

            // Then
            assertThat(calendar.getId()).isNotNull();
            assertThat(calendar.getOwnerId()).isEqualTo(ownerId);
            assertThat(calendar.getTitle()).isEqualTo(title);
            assertThat(calendar.getEvents()).isEmpty();
            assertThat(calendar.getPermissions()).isEmpty();
        }
        @Test
        @DisplayName("캘린더 이름을 수정할 수 있다")
        void updateCalendarTitle() {
            // Given
            UserId ownerId = UserId.newId();
            CalendarTitle title = new CalendarTitle("My Calendar");
            CalendarTitle newTitle = new CalendarTitle("Updated Calendar");

            // When
            Calendar calendar = Calendar.create(ownerId, title);
            calendar.updateTitle(newTitle, ownerId);
            // Then
            assertThat(calendar.getId()).isNotNull();
            assertThat(calendar.getOwnerId()).isEqualTo(ownerId);
            assertThat(calendar.getTitle()).isEqualTo(newTitle);
            assertThat(calendar.getEvents()).isEmpty();
            assertThat(calendar.getPermissions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("이벤트 관리 테스트")
    class EventManagementTest {
        private final UserId ownerId = UserId.newId();
        private final Calendar calendar = Calendar.create(ownerId, new CalendarTitle("Test Calendar"));

        @Test
        @DisplayName("소유자는 이벤트를 추가할 수 있다")
        void ownerCanAddEvent() {
            // Given
            EventTitle title = new EventTitle("Meeting");
            EventDescription description = new EventDescription("Team sync");
            DateTimeRange timeRange = new DateTimeRange(
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1)
            );

            // When
            CalendarEvent event = calendar.addEvent(title, description, timeRange, ownerId);

            // Then
            assertThat(calendar.getEvents()).contains(event);
            assertThat(event.getTitle()).isEqualTo(title);
            assertThat(event.getDescription()).isEqualTo(description);
            assertThat(event.getTimeRange()).isEqualTo(timeRange);
            assertThat(event.getStatus()).isEqualTo(EventStatus.SCHEDULED);
        }

        @Test
        @DisplayName("권한이 없는 사용자는 이벤트를 추가할 수 없다")
        void unauthorizedUserCannotAddEvent() {
            // Given
            UserId unauthorizedUser = UserId.newId();
            EventTitle title = new EventTitle("Meeting");
            EventDescription description = new EventDescription("Team sync");
            DateTimeRange timeRange = new DateTimeRange(
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1)
            );

            // When & Then
            assertThatThrownBy(() ->
                    calendar.addEvent(title, description, timeRange, unauthorizedUser)
            )
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Insufficient permissions");
        }

        @Test
        @DisplayName("쓰기 권한이 있는 사용자는 이벤트를 추가할 수 있다")
        void userWithWritePermissionCanAddEvent() {
            // Given
            UserId authorizedUser = UserId.newId();
            calendar.addPermission(authorizedUser, PermissionLevel.WRITE, ownerId);

            EventTitle title = new EventTitle("Meeting");
            EventDescription description = new EventDescription("Team sync");
            DateTimeRange timeRange = new DateTimeRange(
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1)
            );

            // When & Then
            assertDoesNotThrow(() ->
                    calendar.addEvent(title, description, timeRange, authorizedUser)
            );
        }
    }

    @Nested
    @DisplayName("이벤트 수정 테스트")
    class EventUpdateTest {
        private final UserId ownerId = UserId.newId();
        private final Calendar calendar = Calendar.create(ownerId, new CalendarTitle("Test Calendar"));
        private CalendarEvent event;

        @Test
        @DisplayName("소유자는 이벤트를 수정할 수 있다")
        void ownerCanUpdateEvent() {
            // Given
            event = calendar.addEvent(
                    new EventTitle("Original Title"),
                    new EventDescription("Original Description"),
                    new DateTimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
                    ownerId
            );

            EventTitle newTitle = new EventTitle("Updated Title");
            EventDescription newDescription = new EventDescription("Updated Description");
            DateTimeRange newTimeRange = new DateTimeRange(
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(1)
            );

            // When
            calendar.updateEvent(event.getId(), newTitle, newDescription, newTimeRange, ownerId);

            // Then
            CalendarEvent updatedEvent = calendar.getEvents().stream()
                    .filter(e -> e.getId().equals(event.getId()))
                    .findFirst()
                    .orElseThrow();

            assertThat(updatedEvent.getTitle()).isEqualTo(newTitle);
            assertThat(updatedEvent.getDescription()).isEqualTo(newDescription);
            assertThat(updatedEvent.getTimeRange()).isEqualTo(newTimeRange);
        }

        @Test
        @DisplayName("존재하지 않는 이벤트는 수정할 수 없다")
        void cannotUpdateNonExistentEvent() {
            // Given
            CalendarEventId nonExistentEventId = CalendarEventId.newId();

            // When & Then
            assertThatThrownBy(() ->
                    calendar.updateEvent(
                            nonExistentEventId,
                            new EventTitle("New Title"),
                            new EventDescription("New Description"),
                            new DateTimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
                            ownerId
                    )
            )
                    .isInstanceOf(EventNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("권한 관리 테스트")
    class PermissionManagementTest {
        private final UserId ownerId = UserId.newId();
        private final Calendar calendar = Calendar.create(ownerId, new CalendarTitle("Test Calendar"));

        @Test
        @DisplayName("소유자는 다른 사용자에게 권한을 부여할 수 있다")
        void ownerCanGrantPermission() {
            // Given
            UserId userId = UserId.newId();
            PermissionLevel level = PermissionLevel.WRITE;

            // When
            calendar.addPermission(userId, level, ownerId);

            // Then
            assertThat(calendar.getPermissions())
                    .anyMatch(permission ->
                            permission.getUserId().equals(userId) &&
                                    permission.getLevel().equals(level)
                    );
        }

        @Test
        @DisplayName("일반 사용자는 권한을 부여할 수 없다")
        void regularUserCannotGrantPermission() {
            // Given
            UserId regularUser = UserId.newId();
            UserId newUser = UserId.newId();

            // When & Then
            assertThatThrownBy(() ->
                    calendar.addPermission(newUser, PermissionLevel.READ, regularUser)
            )
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("Value Object 테스트")
    class ValueObjectTest {

        @Test
        @DisplayName("캘린더 제목은 빈 값이 될 수 없다")
        void calendarTitleCannotBeEmpty() {
            assertThatThrownBy(() -> new CalendarTitle(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Calendar title cannot be empty");

            assertThatThrownBy(() -> new CalendarTitle("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Calendar title cannot be empty");
        }

        @Test
        @DisplayName("이벤트 시간 범위는 유효해야 한다")
        void eventTimeRangeMustBeValid() {
            LocalDateTime now = LocalDateTime.now();

            assertThatThrownBy(() ->
                    new DateTimeRange(now, now.minusHours(1))
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Start time cannot be after end time");
        }
    }
}