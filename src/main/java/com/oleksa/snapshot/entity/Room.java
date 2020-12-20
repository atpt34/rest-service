package com.oleksa.snapshot.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@Document(indexName = "room", createIndex = false)
public class Room {

    @Id
    private UUID id;
    @NotNull
    private String name;
    private String location;
}
