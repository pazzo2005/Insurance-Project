package com.akinluyi.claims.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.akinluyi.claims.common.domain.ClaimType;
import com.akinluyi.claims.domain.Claim;
import com.akinluyi.claims.exception.ClaimNotFoundException;
import com.akinluyi.claims.exception.InvalidClaimStateException;
import com.akinluyi.claims.service.ClaimService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClaimController.class)
@Import(GlobalExceptionHandler.class)
class ClaimControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClaimService service;

    private Claim sampleClaim() {
        return new Claim("CLM-ABC123", "POL-9", "Jane Doe", ClaimType.AUTO,
                "Collision", new BigDecimal("1500.00"));
    }

    @Test
    void submit_valid_request_returns_201() throws Exception {
        when(service.submit(any())).thenReturn(sampleClaim());
        String body = "{\"policyNumber\":\"POL-9\",\"claimantName\":\"Jane Doe\","
                + "\"type\":\"AUTO\",\"description\":\"Collision\",\"amount\":1500.00}";

        mvc.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimantName").value("Jane Doe"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void submit_invalid_request_returns_400() throws Exception {
        String body = "{\"policyNumber\":\"\",\"type\":\"AUTO\",\"amount\":100}";
        mvc.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void get_unknown_claim_returns_404() throws Exception {
        when(service.getById(eq(9L))).thenThrow(new ClaimNotFoundException(9L));
        mvc.perform(get("/api/claims/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void approve_in_invalid_state_returns_409() throws Exception {
        when(service.approve(eq(1L))).thenThrow(new InvalidClaimStateException("already approved"));
        mvc.perform(post("/api/claims/1/approve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
