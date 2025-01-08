package com.creatorhub.platform.calendar.adapter.out.persistence;

import com.creatorhub.platform.calendar.domain.vo.PermissionLevel;
import com.creatorhub.platform.calendar.domain.vo.UserId;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class CalendarPermissionEntity {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false))
    })
    private UserId userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionLevel level;
}