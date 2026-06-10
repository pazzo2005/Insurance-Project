package com.akinluyi.claims.web;

import com.akinluyi.claims.common.domain.ClaimStatus;
import com.akinluyi.claims.dto.ClaimResponse;
import com.akinluyi.claims.dto.RejectClaimRequest;
import com.akinluyi.claims.dto.SubmitClaimRequest;
import com.akinluyi.claims.service.ClaimService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST API for submitting, listing, and adjudicating insurance claims. */
@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService service;

    public ClaimController(ClaimService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> submit(@Valid @RequestBody SubmitClaimRequest request) {
        ClaimResponse response = ClaimResponse.from(service.submit(request));
        return ResponseEntity.created(URI.create("/api/claims/" + response.id())).body(response);
    }

    @GetMapping
    public List<ClaimResponse> list(@RequestParam(required = false) ClaimStatus status) {
        return service.list(status).stream().map(ClaimResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ClaimResponse get(@PathVariable Long id) {
        return ClaimResponse.from(service.getById(id));
    }

    @PostMapping("/{id}/approve")
    public ClaimResponse approve(@PathVariable Long id) {
        return ClaimResponse.from(service.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ClaimResponse reject(@PathVariable Long id, @Valid @RequestBody RejectClaimRequest request) {
        return ClaimResponse.from(service.reject(id, request.reason()));
    }
}
