package com.leavemanagement.leave_api.leaves.entity;

import com.leavemanagement.leave_api.leaves.enums.LeaveStatus;
import com.leavemanagement.leave_api.leaves.enums.LeaveTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leaves")
@Getter
@Setter
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveTypes leaveType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    public Leave() {
    }

    public Leave(UUID employeeId, LeaveTypes leaveType, LocalDateTime startDate, LocalDateTime endDate, String reason, LeaveStatus leaveStatus) {
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = leaveStatus;
    }
}
