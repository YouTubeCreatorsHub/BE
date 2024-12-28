package com.creatorhub.platform.member.domain.entity;

import com.creatorhub.platform.member.domain.vo.MemberId;
import com.creatorhub.platform.member.domain.vo.MemberName;
import com.creatorhub.platform.member.domain.vo.MemberStatus;
import com.creatorhub.platform.member.domain.vo.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Member {
    private final MemberId id;
    private MemberName name;
    private Password password;
    private MemberStatus status;

    public static Member create(MemberId id, MemberName name, Password password) {
        return new Member(id, name, password, MemberStatus.ACTIVE);
    }

    public void updateName(MemberName name) {
        this.name = name;
    }

    public void updatePassword(Password password) {
        this.password = password;
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
    }

}
