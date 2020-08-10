package com.oleksa.snapshot;

import com.oleksa.snapshot.entity.Room;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
public class RoomController {

    public static final UUID FIRST = new UUID(0, 0);

    private HashMap<UUID, Room> roomMap = new HashMap<>();

    public RoomController() {
        roomMap.put(FIRST, Room.builder().id(FIRST).name("first").build());
    }

    @GetMapping(value = "/room/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoom(@PathVariable("roomId") String roomId) {
        System.out.println("id = " + roomId);
        return roomMap.getOrDefault(UUID.fromString(roomId), roomMap.get(FIRST));
    }

    @PostMapping(value = "/room", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UUID postNewRoom(@RequestBody Room room) {
        UUID uuid = UUID.randomUUID();
        Room newRoom = Room.builder().id(uuid).name(room.getName()).build();
        roomMap.put(uuid, newRoom);
        return uuid;
    }

    @DeleteMapping("/room")
    public ResponseEntity<Void> remove(@RequestParam("roomId") UUID roomId) {
        roomMap.remove(roomId);
        return ResponseEntity.ok().header("abc", "xyz4321").build();
    }

    @PatchMapping(value = "/room/{roomId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Room update(@PathVariable("roomId") String roomId, @RequestBody Room room) {
        Room room1 = roomMap.get(UUID.fromString(roomId));
        if (room1 == null)
            throw new IllegalArgumentException("no room!");
        Room updated = Room.builder().id(room1.getId())
                .name(room.getName()).build();
        roomMap.put(room1.getId(), updated);
        return updated;
    }
}
