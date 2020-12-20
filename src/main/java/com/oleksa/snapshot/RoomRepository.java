package com.oleksa.snapshot;

import com.oleksa.snapshot.entity.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

interface RoomRepository extends CrudRepository<Room, UUID> {

    List<Room> findByName(String name);
}
