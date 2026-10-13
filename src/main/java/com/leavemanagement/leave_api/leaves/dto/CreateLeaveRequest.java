package com.leavemanagement.leave_api.leaves.dto;

import com.leavemanagement.leave_api.leaves.enums.LeaveStatus;
import com.leavemanagement.leave_api.leaves.enums.LeaveTypes;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateLeaveRequest (
        @NotNull UUID employeeId,
        @NotNull LeaveTypes leaveType,
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate,
        @NotBlank String reason,
        @NotNull LeaveStatus status
        ) { }
