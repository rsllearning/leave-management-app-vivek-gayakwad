package com.leavemanagement.leave_api.leaves.controllers;

import com.leavemanagement.leave_api.leaves.dto.CreateLeaveRequest;
import com.leavemanagement.leave_api.leaves.entity.Leave;
import com.leavemanagement.leave_api.leaves.enums.LeaveStatus;
import com.leavemanagement.leave_api.leaves.enums.LeaveTypes;
import com.leavemanagement.leave_api.leaves.services.LeavesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeavesController.class)
class LeavesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeavesService leavesService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void shouldReturnAllLeaves() throws Exception {
        UUID employeeId = UUID.randomUUID();
        Leave leave = new Leave(employeeId, LeaveTypes.PAID,
                LocalDateTime.of(2026, 10, 10, 9, 0),
                LocalDateTime.of(2026, 10, 12, 17, 0),
                "Family trip",
                LeaveStatus.PENDING);
        leave.setId(UUID.randomUUID());

        Mockito.when(leavesService.getAll()).thenReturn(List.of(leave));

        mockMvc.perform(get("/leaves"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reason").value("Family trip"));
    }

    @Test
    void shouldCreateLeave() throws Exception {
        UUID employeeId = UUID.randomUUID();
        CreateLeaveRequest request = new CreateLeaveRequest(
                employeeId,
                LeaveTypes.PAID,
                LocalDateTime.of(2026, 10, 10, 9, 0),
                LocalDateTime.of(2026, 10, 12, 17, 0),
                "Family trip",
                LeaveStatus.PENDING
        );

        Mockito.when(leavesService.create(any(CreateLeaveRequest.class))).thenAnswer(invocation -> {
            CreateLeaveRequest input = invocation.getArgument(0);
            return new com.leavemanagement.leave_api.leaves.dto.LeaveResponse(
                    UUID.randomUUID(),
                    input.employeeId(),
                    input.leaveType(),
                    input.startDate(),
                    input.endDate(),
                    input.reason(),
                    input.status()
            );
        });

        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.reason").value("Family trip"));
    }

    @Test
    void shouldReturnLeaveById() throws Exception {
        UUID leaveId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Leave leave = new Leave(employeeId, LeaveTypes.UNPAID,
                LocalDateTime.of(2026, 11, 1, 9, 0),
                LocalDateTime.of(2026, 11, 3, 17, 0),
                "Personal work",
                LeaveStatus.APPROVED);
        leave.setId(leaveId);

        Mockito.when(leavesService.get(leaveId)).thenReturn(leave);

        mockMvc.perform(get("/leaves/{id}", leaveId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leaveId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldUpdateLeave() throws Exception {
        UUID leaveId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        CreateLeaveRequest request = new CreateLeaveRequest(
                employeeId,
                LeaveTypes.CASUAL,
                LocalDateTime.of(2026, 12, 1, 9, 0),
                LocalDateTime.of(2026, 12, 4, 17, 0),
                "Updated reason",
                LeaveStatus.APPROVED
        );

        Mockito.when(leavesService.update(eq(leaveId), any(CreateLeaveRequest.class)))
                .thenReturn(new com.leavemanagement.leave_api.leaves.dto.LeaveResponse(
                        leaveId,
                        employeeId,
                        LeaveTypes.CASUAL,
                        request.startDate(),
                        request.endDate(),
                        request.reason(),
                        request.status()
                ));

        mockMvc.perform(put("/leaves/{id}", leaveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Updated reason"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldDeleteLeave() throws Exception {
        UUID leaveId = UUID.randomUUID();

        Mockito.doNothing().when(leavesService).delete(leaveId);

        mockMvc.perform(delete("/leaves/{id}", leaveId))
                .andExpect(status().isNoContent());
    }
}
