package org.fzl.eventcqrsaxonapp.query.controllers;

import org.axonframework.messaging.queryhandling.gateway.QueryGateway;
import org.fzl.eventcqrsaxonapp.query.dtos.AccountStatementResponseDTO; // Import the DTO
import org.fzl.eventcqrsaxonapp.query.entities.Account;
import org.fzl.eventcqrsaxonapp.query.queries.GetAccountStatementQuery;
import org.fzl.eventcqrsaxonapp.query.queries.GetAllAccountQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return queryGateway.queryMany(new GetAllAccountQuery(), Account.class);
    }

    @GetMapping("/accountStatement/{accountId}")
    public CompletableFuture<AccountStatementResponseDTO> getAccountStatement(@PathVariable String accountId){
        // Use the DTO class here to match the handler's return type
        return queryGateway.query(new GetAccountStatementQuery(accountId), AccountStatementResponseDTO.class);
    }
}