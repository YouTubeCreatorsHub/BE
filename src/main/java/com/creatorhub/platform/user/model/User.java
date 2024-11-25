package com.creatorhub.platform.user.model;

import com.creatorhub.platform.user.model.vo.Email;
import com.creatorhub.platform.user.model.vo.Name;
import com.creatorhub.platform.user.model.vo.UserId;
import com.creatorhub.platform.user.model.vo.UserStatus;

public class User {
    private final UserId id;
    private Email email;
    private Name name;
    private UserStatus status;

    public User(UserId id, Email email, Name name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.status = UserStatus.ACTIVE;
    }
}
