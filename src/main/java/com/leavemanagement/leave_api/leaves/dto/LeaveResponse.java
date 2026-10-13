package com.leavemanagement.leave_api.leaves.dto;

import com.leavemanagement.leave_api.leaves.enums.LeaveStatus;
import com.leavemanagement.leave_api.leaves.enums.LeaveTypes;
import jakarta.annotation.Nonnull;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeaveResponse(
        @Nonnull UUID id,
        @Nonnull UUID employeeId,
        LeaveTypes leaveType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String reason,
        LeaveStatus status
        ) {
}
