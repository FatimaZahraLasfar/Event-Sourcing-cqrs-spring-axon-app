
package org.fzl.eventcqrsaxonapp.commands.aggregates;

import lombok.extern.slf4j.Slf4j;

import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;

import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.CreditAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;
import org.fzl.eventcqrsaxonapp.events.AccountCreatedEvent;
import org.fzl.eventcqrsaxonapp.events.AccountCreditedEvent;

@EventSourced(tagKey = "accountId")
@Slf4j
public class AccountAggregate {
    private String accountId;
    private double balance;
    private AccountStatus status;
    private String currency;

    // Constructor used by Axon
    @EntityCreator
    public AccountAggregate() {
    }

    // Handle AddAccountCommand
    @CommandHandler
    public static String handle(
            AddAccountCommand command,
            EventAppender eventAppender) {

        log.info("############ AddAccountCommand RECEIVED ############");
        log.info("Account ID: {}", command.getId());
        log.info("Initial Balance: {}", command.getInitialBalance());
        log.info("Currency: {}", command.getCurrency());

        if (command.getInitialBalance() <= 0) {
            log.error("Account creation FAILED: Initial balance must be positive");
            throw new IllegalArgumentException("Balance must be positive");
        }

        AccountCreatedEvent event = new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                AccountStatus.CREATED,
                command.getCurrency()
        );

        eventAppender.append(event);

        log.info("############ AccountCreatedEvent APPENDED ############");

        return command.getId();
    }

    // Handle AccountCreatedEvent
    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {

        log.info("############ AccountCreatedEvent OCCURRED ############");
        log.info("Account ID: {}", event.getAccountId());
        log.info("Initial Balance: {}", event.getInitialBalance());
        log.info("Status: {}", event.getStatus());
        log.info("Currency: {}", event.getCurrency());

        // Update aggregate state
        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.status = event.getStatus();
        this.currency = event.getCurrency();

        log.info("AccountAggregate state UPDATED successfully");
    }

    // Handle CreateAccountCommand
    @CommandHandler
    public void  handle(
            CreditAccountCommand command,
            EventAppender eventAppender) {

        log.info("############ CreditAccountCommand RECEIVED ############");
//        log.info("Account ID: {}", command.getId());
//        log.info("Initial Balance: {}", command.getInitialBalance());
//        log.info("Currency: {}", command.getCurrency());
        if(!status.equals(AccountStatus.ACTIVATED)) throw new RuntimeException("The account "+command.getId() + " is not activated");
        if (command.getAmount() <= 0) {
            log.error("Account creation FAILED: Initial balance must be positive");
            throw new IllegalArgumentException("Amount must be positive");
        }

        AccountCreditedEvent event = new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        );

        eventAppender.append(event);

        log.info("############ AccountCreatedEvent APPENDED ############");
    }

    // Handle AccountCreditedEvent
    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {

        log.info("############ AccountCreditedEvent OCCURRED ############");
        log.info("Account ID: {}", event.getAccountId());
        log.info("Amount: {}", event.getAmount());
        log.info("Currency: {}", event.getCurrency());

        // Update aggregate state
        this.accountId = event.getAccountId();
        this.balance = this.balance + event.getAmount();
        this.currency = event.getCurrency();
    }

}
