package com.leavemanagement.leave_api.leaves.services;

import com.leavemanagement.leave_api.leaves.Repository.LeavesRepository;
import com.leavemanagement.leave_api.leaves.dto.CreateLeaveRequest;
import com.leavemanagement.leave_api.leaves.dto.LeaveResponse;
import com.leavemanagement.leave_api.leaves.entity.Leave;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class LeavesService {

    private final LeavesRepository leavesRepository;

    public LeavesService(LeavesRepository leavesRepository) {
        this.leavesRepository = leavesRepository;
    }

    public List<Leave> getAll() {
        return this.leavesRepository.findAll();
    }

    public Leave get(UUID id) {
        return this.leavesRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any leave request with id " + id)
        );
    }

    public LeaveResponse create(CreateLeaveRequest leaveRequest) {
        validateLeaveDates(leaveRequest.startDate(), leaveRequest.endDate());

        Leave leaveEntity = new Leave(
                leaveRequest.employeeId(),
                leaveRequest.leaveType(),
                leaveRequest.startDate(),
                leaveRequest.endDate(),
                leaveRequest.reason(),
                leaveRequest.status()
        );
        Leave created = this.leavesRepository.save(leaveEntity);
        return toResponse(created);
    }

    @Transactional
    public LeaveResponse update(UUID id, CreateLeaveRequest leaveRequest) {
        validateLeaveDates(leaveRequest.startDate(), leaveRequest.endDate());

        Leave leave = this.leavesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Leave request not found for id " + id));
        leave.setLeaveType(leaveRequest.leaveType());
        leave.setStartDate(leaveRequest.startDate());
        leave.setEndDate(leaveRequest.endDate());
        leave.setReason(leaveRequest.reason());
        leave.setStatus(leaveRequest.status());

        return toResponse(leave);
    }

    public void delete(UUID id) {
        this.leavesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Leave request not found for id " + id));
        this.leavesRepository.deleteById(id);
    }

    private void validateLeaveDates(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }
    }

    private LeaveResponse toResponse(Leave leave) {
        return new LeaveResponse(
                leave.getId(),
                leave.getEmployeeId(),
                leave.getLeaveType(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getReason(),
                leave.getStatus()
        );
    }
}
