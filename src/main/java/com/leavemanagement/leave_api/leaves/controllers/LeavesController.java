package com.leavemanagement.leave_api.leaves.controllers;

import com.leavemanagement.leave_api.leaves.dto.CreateLeaveRequest;
import com.leavemanagement.leave_api.leaves.dto.LeaveResponse;
import com.leavemanagement.leave_api.leaves.entity.Leave;
import com.leavemanagement.leave_api.leaves.services.LeavesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leaves")
public class LeavesController {

    private final LeavesService leavesService;

    public LeavesController(LeavesService leavesService) {
        this.leavesService = leavesService;
    }

    @GetMapping
    public List<LeaveResponse> getAll() {
        return this.leavesService.getAll()
                .stream().map(leave -> new LeaveResponse(
                        leave.getId(),
                        leave.getEmployeeId(),
                        leave.getLeaveType(),
                        leave.getStartDate(),
                        leave.getEndDate(),
                        leave.getReason(),
                        leave.getStatus()
                )).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public LeaveResponse get(@PathVariable UUID id) {
        Leave leave = this.leavesService.get(id);
        if (leave != null) {
            return new LeaveResponse(
                    leave.getId(),
                    leave.getEmployeeId(),
                    leave.getLeaveType(),
                    leave.getStartDate(),
                    leave.getEndDate(),
                    leave.getReason(),
                    leave.getStatus()
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found for id " + id);
        }
    }

    @PostMapping
    public ResponseEntity<LeaveResponse> create(@Valid @RequestBody CreateLeaveRequest requestData) {
        LeaveResponse response = this.leavesService.create(requestData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public LeaveResponse update(@PathVariable UUID id, @Valid @RequestBody CreateLeaveRequest createLeaveRequest) {
        return this.leavesService.update(id, createLeaveRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.leavesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
