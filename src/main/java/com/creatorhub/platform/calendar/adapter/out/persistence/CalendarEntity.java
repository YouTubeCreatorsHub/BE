package com.creatorhub.platform.calendar.adapter.out.persistence;

import com.creatorhub.platform.calendar.domain.vo.CalendarId;
import com.creatorhub.platform.calendar.domain.vo.CalendarTitle;
import com.creatorhub.platform.calendar.domain.vo.UserId;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "calendars")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class CalendarEntity {
    @EmbeddedId
    private CalendarId id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "owner_id", nullable = false))
    })
    private UserId ownerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false))
    })
    private CalendarTitle title;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CalendarEventEntity> events = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "calendar_permissions",
            joinColumns = @JoinColumn(name = "calendar_id")
    )
    private Set<CalendarPermissionEntity> permissions = new HashSet<>();
}
