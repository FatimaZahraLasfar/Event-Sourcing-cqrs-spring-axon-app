package org.fzl.eventcqrsaxonapp.query.handlers;

import org.axonframework.messaging.queryhandling.annotation.QueryHandler;
import org.fzl.eventcqrsaxonapp.query.entities.Account;
import org.fzl.eventcqrsaxonapp.query.queries.GetAllAccountQuery;
import org.fzl.eventcqrsaxonapp.query.repository.AccountRepository;
import org.fzl.eventcqrsaxonapp.query.repository.OperationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AccountQueryHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository){
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }
    @QueryHandler
    public List<Account> on(GetAllAccountQuery query){
        return accountRepository.findAll();
    }
}
