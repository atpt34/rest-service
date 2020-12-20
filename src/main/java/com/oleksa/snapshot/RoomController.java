package com.oleksa.snapshot;

import com.oleksa.snapshot.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.Validator;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class RoomController {

    public static final UUID FIRST = new UUID(0, 0);
    @Autowired
    private RandomCaller randomCaller;
    @Autowired
    private Validator validator;
//    @Autowired
//    private RestHighLevelClient client;
    @Autowired
    private RoomRepository repository;

    @GetMapping(value = "/room/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoomByName(@RequestParam("name") String name) throws IOException {
        return repository.findByName(name).get(0);
    }

    @GetMapping(value = "/room/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoom(@PathVariable("roomId") String roomId) throws IOException {
        System.out.println("id = " + roomId);
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        RandomResponse randomResponse = randomCaller.doOld();
//        stopWatch.stop();
//        System.out.println("Took " + stopWatch.getLastTaskTimeMillis() + " ms");
//        System.out.println(randomResponse);
//        GetRequest getRequest = new GetRequest("room");
//        getRequest.id(roomId);
//        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
//        Room room2 = new ObjectMapper().readValue(getResponse.getSourceAsString(), Room.class);
//        return roomMap.getOrDefault(UUID.fromString(roomId), roomMap.get(FIRST));
        return repository.findById(UUID.fromString(roomId)).orElse(null);
    }

    @PostMapping(value = "/room", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UUID postNewRoom(@RequestBody @Valid Room room) throws IOException {
        UUID uuid = UUID.randomUUID();
        Room newRoom = Room.builder().id(uuid).name(room.getName()).build();
//        IndexResponse room1 = client.index(
//                new IndexRequest("room")
//                    .id(uuid.toString())
//                    .source(new ObjectMapper().writeValueAsString(newRoom), XContentType.JSON), RequestOptions.DEFAULT);
//        System.out.println(room1.toString());
        CompletableFuture.runAsync(new AsyncTask(newRoom, randomCaller))
                .thenRun(() -> repository.save(newRoom));
        return uuid;
    }

    @DeleteMapping("/room")
    public ResponseEntity<Void> remove(@RequestParam("roomId") UUID roomId) {
        repository.deleteById(roomId);
        return ResponseEntity.ok().header("abc", "xyz4321").build();
    }

    @PatchMapping(value = "/room/{roomId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Room update(@PathVariable("roomId") String roomId, @RequestBody Room room) {
        if (roomId == null || room == null) {
            throw new IllegalArgumentException("no room!");
        }
        Room existingRoom = repository.findById(UUID.fromString(roomId))
                .orElseThrow(() -> new IllegalArgumentException("no room!"));
        if (room.getName() != null) {
            Room updated = Room.builder()
                    .id(existingRoom.getId())
                    .name(room.getName())
                    .location(existingRoom.getLocation())
                    .build();
            repository.save(updated);
            return updated;
        }
        return existingRoom;
    }

    @PutMapping(value = "/room/{roomId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Room put(@PathVariable("roomId") String roomId, @RequestBody Room room) {
        if (roomId == null || room == null) {
            throw new IllegalArgumentException("no room!");
        }
        repository.save(room);
        return room;
    }
}
