package org.fzl.eventcqrsaxonapp.commands.aggregates;

import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;

import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;
import org.fzl.eventcqrsaxonapp.events.AccountCreatedEvent;

@EventSourced
public class AccountAggregate {

    private String accountId;
    private double balance;
    private AccountStatus status;

    @EntityCreator
    public AccountAggregate() {
    }

    @CommandHandler
    public static AccountAggregate handle(
            AddAccountCommand command,
            EventAppender eventAppender) {

        if (command.getInitialBalance() <= 0) {
            throw new IllegalArgumentException(
                    "Balance must be positive"
            );
        }

        eventAppender.append(
                new AccountCreatedEvent(
                        command.getId(),
                        command.getInitialBalance(),
                        AccountStatus.CREATED,
                        command.getCurrency()
                )
        );

        return new AccountAggregate();
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.status = event.getStatus();
    }
}