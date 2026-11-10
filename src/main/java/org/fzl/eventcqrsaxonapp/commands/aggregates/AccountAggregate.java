package org.fzl.eventcqrsaxonapp.commands.aggregates;

import lombok.extern.slf4j.Slf4j;

import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;

import org.fzl.eventcqrsaxonapp.commands.commands.AddAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.CreditAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.DebitAccountCommand;
import org.fzl.eventcqrsaxonapp.commands.commands.UpdateAccountStatusCommand;
import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;

import org.fzl.eventcqrsaxonapp.events.*;

@EventSourced(tagKey = "accountId")
@Slf4j
public class AccountAggregate {

    private String accountId;
    private double balance;
    private AccountStatus status;
    private String currency;

    @EntityCreator
    public AccountAggregate() {
    }

    // ==========================================
    // CREATE ACCOUNT
    // ==========================================

    @CommandHandler
    public static String handle(
            AddAccountCommand command,
            EventAppender eventAppender
    ) {

        log.info("############ AddAccountCommand RECEIVED ############");
        log.info("Account ID: {}", command.getId());
        log.info("Initial Balance: {}", command.getInitialBalance());
        log.info("Currency: {}", command.getCurrency());

        if (command.getInitialBalance() <= 0) {
            throw new IllegalArgumentException(
                    "Initial balance must be positive"
            );
        }

        AccountCreatedEvent createdEvent = new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                AccountStatus.CREATED,
                command.getCurrency()
        );

        AccountActivatedEvent activatedEvent = new AccountActivatedEvent(
                command.getId(),
                AccountStatus.ACTIVATED
        );

        eventAppender.append(createdEvent);
        eventAppender.append(activatedEvent);

        log.info("AccountCreatedEvent and AccountActivatedEvent APPENDED");

        return command.getId();
    }

    // ==========================================
    // ACCOUNT CREATED EVENT
    // ==========================================

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {

        log.info("############ AccountCreatedEvent OCCURRED ############");

        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.status = event.getStatus();
        this.currency = event.getCurrency();

        log.info("AccountAggregate initialized successfully");
    }

    // ==========================================
    // ACCOUNT ACTIVATED EVENT
    // ==========================================

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {

        log.info("############ AccountActivatedEvent OCCURRED ############");

        this.accountId = event.getAccountId();
        this.status = event.getStatus();

        log.info("Account {} is now {}", accountId, status);
    }

    // ==========================================
    // CREDIT ACCOUNT COMMAND
    // ==========================================

    @CommandHandler
    public void handle(
            CreditAccountCommand command,
            EventAppender eventAppender
    ) {

        log.info("############ CreditAccountCommand RECEIVED ############");
        log.info("Account ID: {}", command.getId());
        log.info("Amount: {}", command.getAmount());
        log.info("Currency: {}", command.getCurrency());

        // Check account status
        if (status != AccountStatus.ACTIVATED) {
            throw new IllegalStateException(
                    "The account " + command.getId() + " is not activated"
            );
        }

        // Check amount
        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Credit amount must be positive"
            );
        }

        // Check currency
        if (!currency.equalsIgnoreCase(command.getCurrency())) {
            throw new IllegalArgumentException(
                    "Currency does not match the account currency"
            );
        }

        AccountCreditedEvent creditedEvent = new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        );

        eventAppender.append(creditedEvent);

        log.info("############ AccountCreditedEvent APPENDED ############");
    }

    // ==========================================
    // ACCOUNT CREDITED EVENT
    // ==========================================

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {

        log.info("############ AccountCreditedEvent OCCURRED ############");

        this.accountId = event.getAccountId();
        this.balance += event.getAmount();

        log.info("Account ID: {}", accountId);
        log.info("Credited amount: {}", event.getAmount());
        log.info("New balance: {}", balance);
    }

// ==========================================
    // DEBIT ACCOUNT COMMAND
    // ==========================================

    @CommandHandler
    public void handle(
            DebitAccountCommand command,
            EventAppender eventAppender
    ) {

        log.info("############ DebitAccountCommand RECEIVED ############");
        log.info("Account ID: {}", command.getId());
        log.info("Amount: {}", command.getAmount());
        log.info("Currency: {}", command.getCurrency());

        // Check account status
        if (status != AccountStatus.ACTIVATED) {
            throw new IllegalStateException(
                    "The account " + command.getId() + " is not activated"
            );
        }
        if (balance < command.getAmount()) {
            throw new RuntimeException(
                    "The balance is not sufficient Exception"
            );
        }
        // Check amount
        if (command.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Credit amount must be positive"
            );
        }

        // Check currency
        if (!currency.equalsIgnoreCase(command.getCurrency())) {
            throw new IllegalArgumentException(
                    "Currency does not match the account currency"
            );
        }

        AccountDebitedEvent creditedEvent = new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        );

        eventAppender.append(creditedEvent);

        log.info("############ AccountCreditedEvent APPENDED ############");
    }

    // ==========================================
    // ACCOUNT DEBITED EVENT
    // ==========================================

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {

        log.info("############ AccountDebitedEvent OCCURRED ############");

        this.accountId = event.getAccountId();
        this.balance -= event.getAmount();

        log.info("Account ID: {}", accountId);
        log.info("Credited amount: {}", event.getAmount());
        log.info("New balance: {}", balance);
    }

    // ==========================================
    // UPDATE ACCOUNT STATUS COMMAND
    // ==========================================

    @CommandHandler
    public void handle(
            UpdateAccountStatusCommand command,
            EventAppender eventAppender
    ) {

        log.info("############ DebitAccountCommand RECEIVED ############");
        log.info("Account ID: {}", command.getId());
        log.info("Status: {}", command.getStatus());

        // Check account status
        if (command.getStatus() == status) {
            throw new RuntimeException(
                    "This account is already in : "+command.getStatus()
            );
        }
        AccountStatusUpdatedEvent updateStatus = new AccountStatusUpdatedEvent(
                command.getId(),
                command.getStatus()
        );

        eventAppender.append(updateStatus);

        log.info("############ AccountCreditedEvent APPENDED ############");
    }

    // ==========================================
    // ACCOUNT UPDATE STATUS EVENT
    // ==========================================

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {

        log.info("############ AccountStatusUpdatedEvent OCCURRED ############");

        this.accountId = event.getAccountId();
        this.status = event.getStatus();

        log.info("Account ID: {}", accountId);
        log.info("New Status: {}", status);
    }
}
