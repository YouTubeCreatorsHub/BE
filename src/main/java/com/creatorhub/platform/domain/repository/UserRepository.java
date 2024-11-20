package com.creatorhub.platform.domain.repository;

import com.creatorhub.platform.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
