package org.fzl.eventcqrsaxonapp.commands.controllers;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.core.MessageStreamUtils;
import org.axonframework.messaging.eventstreaming.StreamingCondition;
import org.axonframework.eventsourcing.eventstore.EventStoreTransaction;
import org.axonframework.eventsourcing.eventstore.SourcingCondition;
import org.axonframework.messaging.eventstreaming.EventCriteria;
import org.axonframework.messaging.eventstreaming.Tag;
import org.axonframework.messaging.core.unitofwork.UnitOfWork;
import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.CreditAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.dtos.AddNewAccountRequestDTO;
import org.fzl.eventcqrsaxonapp.commands.dtos.CreditAccountRequestDTO;
import org.fzl.eventcqrsaxonapp.events.AccountCreatedEvent;
import org.axonframework.messaging.core.unitofwork.UnitOfWorkFactory;
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
            EventStore eventStore, UnitOfWorkFactory unitOfWorkFactory
    ) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.unitOfWorkFactory = unitOfWorkFactory;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(
            @RequestBody AddNewAccountRequestDTO request
    ) {
        String accountId = UUID.randomUUID().toString();

        System.out.println("POST REQUEST RECEIVED");
        System.out.println("Generated Account ID: " + accountId);

        return commandGateway.send(
                new AddAccountCommand(
                        accountId,
                        request.initialBalance(),
                        request.currency()
                ),
                String.class
        );
    }


    @GetMapping("/events/{accountId}")
    public CompletableFuture<List<AccountCreatedEvent>> getAccountEvents(
            @PathVariable String accountId
    ) {
        System.out.println("========== GET EVENTS ==========");
        System.out.println("Account ID: " + accountId);

        EventCriteria criteria =
                EventCriteria.havingTags("accountId", accountId);

        SourcingCondition condition =
                SourcingCondition.conditionFor(criteria);

        return unitOfWorkFactory.create().executeWithResult(context -> {

            System.out.println("ProcessingContext created successfully");

            EventStoreTransaction transaction =
                    eventStore.transaction(context);

            var stream = transaction.source(condition);

            return MessageStreamUtils.reduce(
                    stream,
                    new ArrayList<AccountCreatedEvent>(),
                    (events, entry) -> {

                        System.out.println("========== EVENT RECEIVED ==========");

                        AccountCreatedEvent event =
                                entry.message().payloadAs(AccountCreatedEvent.class);

                        if (event != null) {
                            System.out.println("EVENT DESERIALIZED SUCCESSFULLY");
                            System.out.println("Account ID: " + event.getAccountId());

                            events.add(event);
                        }

                        System.out.println("Events collected: " + events.size());

                        return events;
                    }
            );
        });
    }
    @PostMapping("/credit")
    public CompletableFuture<String> creditAccount(
            @RequestBody CreditAccountRequestDTO request) {

        return commandGateway.send(
                new CreditAccountCommand(
                        request.accountId(),
                        request.amount(),
                        request.currency()
                ),
                String.class
        );
    }

    @ExceptionHandler(Exception.class)
    public String handle(Exception exception) {
        return exception.getMessage();
    }
}