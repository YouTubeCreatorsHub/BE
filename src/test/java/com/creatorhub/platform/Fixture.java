package com.creatorhub.platform;

import com.creatorhub.platform.community.entity.Board;

import java.time.LocalDateTime;

public class Fixture {
    public Board createBoard(String name){
        return Board.builder()
                .id(1L)
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
