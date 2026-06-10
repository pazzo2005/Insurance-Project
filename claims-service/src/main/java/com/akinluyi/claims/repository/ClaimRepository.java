package com.akinluyi.claims.repository;

import com.akinluyi.claims.common.domain.ClaimStatus;
import com.akinluyi.claims.domain.Claim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for {@link Claim} aggregates. */
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);

    List<Claim> findAllByOrderByCreatedAtDesc();

    boolean existsByClaimNumber(String claimNumber);
}
