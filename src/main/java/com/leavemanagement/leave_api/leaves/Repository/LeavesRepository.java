package com.leavemanagement.leave_api.leaves.Repository;

import com.leavemanagement.leave_api.leaves.entity.Leave;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeavesRepository {
    List<Leave> findAll();

    Optional<Leave> findById(UUID id);

    Leave save(Leave leave);

    void deleteById(UUID id);
}
