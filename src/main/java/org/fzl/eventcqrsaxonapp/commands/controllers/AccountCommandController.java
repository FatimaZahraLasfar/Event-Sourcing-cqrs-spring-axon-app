package org.fzl.eventcqrsaxonapp.commands.controllers;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.core.MessageStreamUtils;
import org.axonframework.messaging.eventstreaming.StreamingCondition;

import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.dtos.AddNewAccountRequestDTO;
import org.fzl.eventcqrsaxonapp.events.AccountCreatedEvent;

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

    public AccountCommandController(
            CommandGateway commandGateway,
            EventStore eventStore
    ) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(
            @RequestBody AddNewAccountRequestDTO request
    ) {
        return commandGateway.send(
                new AddAccountCommand(
                        UUID.randomUUID().toString(),
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
        var stream = eventStore.open(
                StreamingCondition.startingFrom(null),
                null
        );

        return MessageStreamUtils.reduce(
                stream,
                new ArrayList<AccountCreatedEvent>(),
                (events, entry) -> {
                    Object payload = entry.message().payload();

                    if (payload instanceof AccountCreatedEvent event
                            && accountId.equals(event.getAccountId())) {
                        events.add(event);
                    }

                    return events;
                }
        );
    }
    @ExceptionHandler(Exception.class)
    public String handle(Exception exception) {
        return exception.getMessage();
    }
}