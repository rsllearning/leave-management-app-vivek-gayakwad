package com.leavemanagement.leave_api.leaves.services;

import com.leavemanagement.leave_api.leaves.Repository.LeavesRepository;
import com.leavemanagement.leave_api.leaves.dto.CreateLeaveRequest;
import com.leavemanagement.leave_api.leaves.dto.LeaveResponse;
import com.leavemanagement.leave_api.leaves.entity.Leave;
import com.leavemanagement.leave_api.leaves.enums.LeaveStatus;
import com.leavemanagement.leave_api.leaves.enums.LeaveTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeavesServiceTest {

    @Mock
    private LeavesRepository leavesRepository;

    @InjectMocks
    private LeavesService leavesService;

    @Test
    void shouldCreateLeaveWhenRequestIsValid() {
        UUID employeeId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.of(2026, 10, 10, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 10, 12, 17, 0);
        CreateLeaveRequest request = new CreateLeaveRequest(
                employeeId,
                LeaveTypes.PAID,
                startDate,
                endDate,
                "Family trip",
                LeaveStatus.PENDING
        );

        when(leavesRepository.save(any(Leave.class))).thenAnswer(invocation -> {
            Leave leave = invocation.getArgument(0);
            leave.setId(UUID.randomUUID());
            return leave;
        });

        LeaveResponse response = leavesService.create(request);

        assertNotNull(response.id());
        assertEquals(employeeId, response.employeeId());
        assertEquals(LeaveTypes.PAID, response.leaveType());
        assertEquals("Family trip", response.reason());
        assertEquals(LeaveStatus.PENDING, response.status());
        verify(leavesRepository).save(any(Leave.class));
    }

    @Test
    void shouldRejectLeaveWhenEndDateIsBeforeStartDate() {
        UUID employeeId = UUID.randomUUID();
        CreateLeaveRequest request = new CreateLeaveRequest(
                employeeId,
                LeaveTypes.CASUAL,
                LocalDateTime.of(2026, 10, 12, 9, 0),
                LocalDateTime.of(2026, 10, 10, 17, 0),
                "Medical leave",
                LeaveStatus.PENDING
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> leavesService.create(request));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("End date cannot be before start date", exception.getReason());
    }

    @Test
    void shouldReturnLeaveWhenIdExists() {
        UUID leaveId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Leave leave = new Leave(employeeId, LeaveTypes.UNPAID,
                LocalDateTime.of(2026, 11, 1, 9, 0),
                LocalDateTime.of(2026, 11, 3, 17, 0),
                "Personal work",
                LeaveStatus.APPROVED);
        leave.setId(leaveId);

        when(leavesRepository.findById(leaveId)).thenReturn(Optional.of(leave));

        Leave actual = leavesService.get(leaveId);

        assertEquals(leaveId, actual.getId());
        assertEquals(employeeId, actual.getEmployeeId());
        assertEquals(LeaveStatus.APPROVED, actual.getStatus());
    }

    @Test
    void shouldDeleteLeaveWhenIdExists() {
        UUID leaveId = UUID.randomUUID();
        Leave leave = new Leave(UUID.randomUUID(), LeaveTypes.PAID,
                LocalDateTime.of(2026, 12, 1, 9, 0),
                LocalDateTime.of(2026, 12, 2, 17, 0),
                "Holiday",
                LeaveStatus.PENDING);
        leave.setId(leaveId);

        when(leavesRepository.findById(leaveId)).thenReturn(Optional.of(leave));

        leavesService.delete(leaveId);

        verify(leavesRepository).deleteById(leaveId);
    }

    @Test
    void shouldGetAllLeaves() {
        Leave first = new Leave(UUID.randomUUID(), LeaveTypes.PAID,
                LocalDateTime.of(2026, 9, 1, 9, 0),
                LocalDateTime.of(2026, 9, 2, 17, 0),
                "Vacation",
                LeaveStatus.PENDING);
        first.setId(UUID.randomUUID());

        Leave second = new Leave(UUID.randomUUID(), LeaveTypes.CASUAL,
                LocalDateTime.of(2026, 9, 5, 9, 0),
                LocalDateTime.of(2026, 9, 6, 17, 0),
                "Family event",
                LeaveStatus.APPROVED);
        second.setId(UUID.randomUUID());

        when(leavesRepository.findAll()).thenReturn(List.of(first, second));

        List<Leave> leaves = leavesService.getAll();

        assertEquals(2, leaves.size());
        assertEquals("Vacation", leaves.get(0).getReason());
        assertEquals("Family event", leaves.get(1).getReason());
    }
}
