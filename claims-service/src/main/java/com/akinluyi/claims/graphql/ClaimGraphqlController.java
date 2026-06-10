package com.akinluyi.claims.graphql;

import com.akinluyi.claims.dto.ClaimResponse;
import com.akinluyi.claims.service.ClaimService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/** GraphQL query resolver exposing claims read access alongside the REST API. */
@Controller
public class ClaimGraphqlController {

    private final ClaimService service;

    public ClaimGraphqlController(ClaimService service) {
        this.service = service;
    }

    @QueryMapping
    public ClaimResponse claim(@Argument Long id) {
        return ClaimResponse.from(service.getById(id));
    }

    @QueryMapping
    public List<ClaimResponse> claims() {
        return service.list(null).stream().map(ClaimResponse::from).toList();
    }
}
