package com.akinluyi.claims.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.akinluyi.claims.common.domain.ClaimStatus;
import com.akinluyi.claims.common.domain.ClaimType;
import com.akinluyi.claims.common.event.ClaimApprovedEvent;
import com.akinluyi.claims.common.event.ClaimRejectedEvent;
import com.akinluyi.claims.common.event.ClaimSubmittedEvent;
import com.akinluyi.claims.domain.Claim;
import com.akinluyi.claims.dto.SubmitClaimRequest;
import com.akinluyi.claims.events.EventPublisher;
import com.akinluyi.claims.exception.ClaimNotFoundException;
import com.akinluyi.claims.exception.InvalidClaimStateException;
import com.akinluyi.claims.repository.ClaimRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository repository;

    @Mock
    private EventPublisher publisher;

    @InjectMocks
    private ClaimService service;

    private Claim newClaim() {
        return new Claim("CLM-TEST01", "POL-1", "Jane Doe", ClaimType.AUTO,
                "Rear-end collision", new BigDecimal("1200.00"));
    }

    @Test
    void submit_persists_and_publishes_submitted_event() {
        when(repository.existsByClaimNumber(anyString())).thenReturn(false);
        when(repository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim result = service.submit(new SubmitClaimRequest(
                "POL-1", "Jane Doe", ClaimType.AUTO, "desc", new BigDecimal("1200.00")));

        assertThat(result.getStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(result.getClaimNumber()).startsWith("CLM-");
        verify(publisher).publish(any(ClaimSubmittedEvent.class));
    }

    @Test
    void getById_missing_throws_not_found() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(99L)).isInstanceOf(ClaimNotFoundException.class);
    }

    @Test
    void approve_submitted_claim_succeeds_and_publishes() {
        when(repository.findById(1L)).thenReturn(Optional.of(newClaim()));
        when(repository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim result = service.approve(1L);

        assertThat(result.getStatus()).isEqualTo(ClaimStatus.APPROVED);
        verify(publisher).publish(any(ClaimApprovedEvent.class));
    }

    @Test
    void approve_already_approved_claim_throws_and_does_not_publish() {
        Claim claim = newClaim();
        claim.approve();
        when(repository.findById(1L)).thenReturn(Optional.of(claim));

        assertThatThrownBy(() -> service.approve(1L))
                .isInstanceOf(InvalidClaimStateException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void reject_sets_reason_and_publishes_rejected_event() {
        when(repository.findById(1L)).thenReturn(Optional.of(newClaim()));
        when(repository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim result = service.reject(1L, "Insufficient documentation");

        assertThat(result.getStatus()).isEqualTo(ClaimStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo("Insufficient documentation");
        verify(publisher).publish(any(ClaimRejectedEvent.class));
    }
}
