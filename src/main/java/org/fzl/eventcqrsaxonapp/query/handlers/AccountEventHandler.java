package org.fzl.eventcqrsaxonapp.query.handlers;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.eventhandling.EventMessage;
import org.axonframework.messaging.eventhandling.annotation.EventHandler;
import org.fzl.eventcqrsaxonapp.events.AccountCreatedEvent;
import org.fzl.eventcqrsaxonapp.query.entities.Account;
import org.fzl.eventcqrsaxonapp.query.repository.AccountRepository;
import org.fzl.eventcqrsaxonapp.query.repository.OperationRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountEventHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    public AccountEventHandler (AccountRepository accountRepository, OperationRepository operationRepository){
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }
    @EventHandler
    public void on (AccountCreatedEvent event, EventMessage eventMessage){
        log.info("########## Query Side : AccountCreatedEvent Received ");
        Account account = Account.builder()
                .id(event.getAccountId())
                .balance(event.getInitialBalance())
                .status(event.getStatus())
                .createdAt(eventMessage.timestamp())
                .build();
    }
}
