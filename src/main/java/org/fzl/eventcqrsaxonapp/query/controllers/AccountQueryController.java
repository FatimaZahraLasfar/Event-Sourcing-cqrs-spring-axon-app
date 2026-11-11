package org.fzl.eventcqrsaxonapp.query.controllers;

import org.axonframework.messaging.queryhandling.gateway.QueryGateway;
import org.fzl.eventcqrsaxonapp.query.entities.Account;
import org.fzl.eventcqrsaxonapp.query.queries.GetAllAccountQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query/accounts")
public class AccountQueryController {

    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/all")
    public CompletableFuture<List<Account>> getAllAccount() {
        // Axon 5: use queryMany for multi‑response queries,
        // passing the element class directly instead of a ResponseType.
        return queryGateway.queryMany(new GetAllAccountQuery(), Account.class);
    }
}