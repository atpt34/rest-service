package com.oleksa.snapshot.entity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
public class Room {

    private UUID id;
    private String name;
    private String location;
}
