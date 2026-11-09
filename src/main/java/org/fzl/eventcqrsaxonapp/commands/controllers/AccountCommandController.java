package org.fzl.eventcqrsaxonapp.commands.controllers;

import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.dtos.AddNewAccountRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandController {

    private final CommandGateway commandGateway;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public AccountCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(@RequestBody AddNewAccountRequestDTO request) {
        // In Axon 5, pass the expected result type (String.class) to the send method
        // to get a CompletableFuture<String> directly.
        return commandGateway.send(
                new AddAccountCommand(
                        UUID.randomUUID().toString(),
                        request.initialBalance(),
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