package org.fzl.eventcqrsaxonapp.commands.controllers;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.EventStoreTransaction;
import org.axonframework.eventsourcing.eventstore.SourcingCondition;

import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.core.MessageStreamUtils;
import org.axonframework.messaging.core.unitofwork.UnitOfWorkFactory;
import org.axonframework.messaging.eventstreaming.EventCriteria;

import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.CreditAccountCommand;

import org.fzl.eventcqrsaxonapp.commands.commands.DebitAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.UpdateAccountStatusCommand;
import org.fzl.eventcqrsaxonapp.commands.dtos.AddNewAccountRequestDTO;
import org.fzl.eventcqrsaxonapp.commands.dtos.CreditAccountRequestDTO;

import java.util.Map;

import org.fzl.eventcqrsaxonapp.commands.dtos.DebitAccountRequestDTO;
import org.fzl.eventcqrsaxonapp.commands.dtos.UpdateAccountRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandController {

    private final CommandGateway commandGateway;
    private final EventStore eventStore;
    private final UnitOfWorkFactory unitOfWorkFactory;

    public AccountCommandController(
            CommandGateway commandGateway,
            EventStore eventStore,
            UnitOfWorkFactory unitOfWorkFactory
    ) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.unitOfWorkFactory = unitOfWorkFactory;
    }

    // ==========================================
    // CREATE ACCOUNT
    // ==========================================

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(
            @RequestBody AddNewAccountRequestDTO request
    ) {

        String accountId = UUID.randomUUID().toString();

        System.out.println("========== ADD ACCOUNT ==========");
        System.out.println("Generated Account ID: " + accountId);

        AddAccountCommand command = new AddAccountCommand(
                accountId,
                request.initialBalance(),
                request.currency()
        );

        return commandGateway.send(command, String.class);
    }

    // ==========================================
    // GET ACCOUNT EVENTS
    // ==========================================

    @GetMapping("/events/{accountId}")
    public CompletableFuture<List<Map<?, ?>>> getAccountEvents(
            @PathVariable String accountId
    ) {

        System.out.println("========== GET EVENTS ==========");
        System.out.println("Account ID: " + accountId);

        EventCriteria criteria =
                EventCriteria.havingTags("accountId", accountId);

        SourcingCondition condition =
                SourcingCondition.conditionFor(criteria);

        return unitOfWorkFactory.create().executeWithResult(context -> {

            EventStoreTransaction transaction =
                    eventStore.transaction(context);

            var stream = transaction.source(condition);

            return MessageStreamUtils.reduce(
                    stream,
                    new ArrayList<Map<?, ?>>(),
                    (events, entry) -> {

                        Map<?, ?> event =
                                entry.message().payloadAs(Map.class);

                        if (event != null) {
                            System.out.println("Retrieved event: " + event);
                            events.add(event);
                        }

                        return events;
                    }
            );
        });
    }

    // ==========================================
    // CREDIT ACCOUNT
    // ==========================================

    @PostMapping("/credit")
    public CompletableFuture<Void> creditAccount(
            @RequestBody CreditAccountRequestDTO request) {

        CreditAccountCommand command = new CreditAccountCommand(
                request.accountId(),
                request.amount(),
                request.currency()
        );

        return commandGateway.send(command, Void.class);
    }
    // ==========================================
    // DEBIT ACCOUNT
    // ==========================================

    @PostMapping("/debit")
    public CompletableFuture<Void> debitAccount(
            @RequestBody DebitAccountRequestDTO request) {

        DebitAccountCommand command = new DebitAccountCommand(
                request.accountId(),
                request.amount(),
                request.currency()
        );

        return commandGateway.send(command, Void.class);
    }
    @PutMapping("/updateStatus")
    public CompletableFuture<Void> updateStatus(
            @RequestBody UpdateAccountRequestDTO request) {

        UpdateAccountStatusCommand command = new UpdateAccountStatusCommand(
                request.accountId(),
                request.status()

        );

        return commandGateway.send(command, Void.class);
    }

    // ==========================================
    // EXCEPTION HANDLER
    // ==========================================

    @ExceptionHandler(Exception.class)
    public String handle(Exception exception) {

        return exception.getMessage();
    }
}