package com.enviro.assessment.junior.fanelesibongesithole.controller;

import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.fanelesibongesithole.exception.GlobalExceptionHandler;
import com.enviro.assessment.junior.fanelesibongesithole.service.WithdrawalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WithdrawalController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class WithdrawalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WithdrawalService withdrawalService;

    @Test
    void createWithdrawal_returnsCreatedResponse() throws Exception {
        when(withdrawalService.createWithdrawal(any()))
                .thenReturn(new WithdrawalResponseDto("Withdrawal request submitted", "WDR-2024-003"));

        mockMvc.perform(post("/api/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 50000.00,
                                  "accountId": "acc_001",
                                  "type": "STANDARD",
                                  "reason": "Test"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId").value("WDR-2024-003"))
                .andExpect(jsonPath("$.message").value("Withdrawal request submitted"));
    }

    @Test
    void createWithdrawal_rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/withdrawals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 0,
                                  "accountId": "",
                                  "type": "STANDARD"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }
}
