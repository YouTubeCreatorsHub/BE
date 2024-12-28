package com.creatorhub.platform.member.adapter.out.persistence;

import com.creatorhub.platform.member.domain.vo.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 요구사항
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // Builder와 함께 사용
@Builder
public class MemberEntity {
    @Id
    private String id;
    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;
}
