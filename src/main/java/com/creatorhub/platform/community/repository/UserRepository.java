package com.creatorhub.platform.community.repository;

import com.creatorhub.platform.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
