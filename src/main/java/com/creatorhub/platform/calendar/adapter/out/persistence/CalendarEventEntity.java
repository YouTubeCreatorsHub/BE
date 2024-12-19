package com.creatorhub.platform.calendar.adapter.out.persistence;

import com.creatorhub.platform.calendar.domain.vo.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calendar_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class CalendarEventEntity {
    @EmbeddedId
    private CalendarEventId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private CalendarEntity calendar;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false))
    })
    private EventTitle title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description"))
    })
    private EventDescription description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "start_time", nullable = false)),
            @AttributeOverride(name = "end", column = @Column(name = "end_time", nullable = false))
    })
    private DateTimeRange timeRange;

    @Enumerated(EnumType.STRING)
    private EventStatus status;
}
