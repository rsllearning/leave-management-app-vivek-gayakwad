package com.leavemanagement.leave_api.leaves.Repository;

import com.leavemanagement.leave_api.leaves.entity.Leave;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLeaveRepository implements LeavesRepository {

    private final ConcurrentHashMap<UUID, Leave> leaves = new ConcurrentHashMap<>();

    @Override
    public Leave save(Leave entity) {
        UUID id = entity.getId() == null ? UUID.randomUUID() : entity.getId();
        entity.setId(id);
        leaves.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Leave> findById(UUID uuid) {
        return Optional.ofNullable(leaves.get(uuid));
    }

    @Override
    public List<Leave> findAll() {
        return new ArrayList<>(leaves.values());
    }

    @Override
    public void deleteById(UUID uuid) {
        leaves.remove(uuid);
    }
}
