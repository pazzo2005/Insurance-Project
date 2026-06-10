package com.akinluyi.claims.service;

import com.akinluyi.claims.common.domain.ClaimStatus;
import com.akinluyi.claims.common.event.ClaimApprovedEvent;
import com.akinluyi.claims.common.event.ClaimRejectedEvent;
import com.akinluyi.claims.common.event.ClaimSubmittedEvent;
import com.akinluyi.claims.domain.Claim;
import com.akinluyi.claims.dto.SubmitClaimRequest;
import com.akinluyi.claims.events.EventPublisher;
import com.akinluyi.claims.exception.ClaimNotFoundException;
import com.akinluyi.claims.exception.InvalidClaimStateException;
import com.akinluyi.claims.repository.ClaimRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application service encapsulating the claim lifecycle and its domain events. */
@Service
public class ClaimService {

    private final ClaimRepository repository;
    private final EventPublisher publisher;

    public ClaimService(ClaimRepository repository, EventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Transactional
    public Claim submit(SubmitClaimRequest request) {
        Claim claim = new Claim(
                generateClaimNumber(),
                request.policyNumber(),
                request.claimantName(),
                request.type(),
                request.description(),
                request.amount());
        Claim saved = repository.save(claim);
        publisher.publish(new ClaimSubmittedEvent(
                saved.getId(), saved.getClaimNumber(), saved.getClaimantName(),
                saved.getType(), saved.getAmount(), Instant.now()));
        return saved;
    }

    @Transactional(readOnly = true)
    public Claim getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ClaimNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Claim> list(ClaimStatus status) {
        return status == null
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional
    public Claim approve(Long id) {
        Claim claim = getById(id);
        requireSubmitted(claim, "approve");
        claim.approve();
        Claim saved = repository.save(claim);
        publisher.publish(new ClaimApprovedEvent(saved.getId(), saved.getClaimNumber(), Instant.now()));
        return saved;
    }

    @Transactional
    public Claim reject(Long id, String reason) {
        Claim claim = getById(id);
        requireSubmitted(claim, "reject");
        claim.reject(reason);
        Claim saved = repository.save(claim);
        publisher.publish(new ClaimRejectedEvent(
                saved.getId(), saved.getClaimNumber(), reason, Instant.now()));
        return saved;
    }

    private void requireSubmitted(Claim claim, String action) {
        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new InvalidClaimStateException(
                    "Cannot " + action + " claim " + claim.getClaimNumber()
                            + " in status " + claim.getStatus());
        }
    }

    private String generateClaimNumber() {
        String candidate;
        do {
            candidate = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (repository.existsByClaimNumber(candidate));
        return candidate;
    }
}
