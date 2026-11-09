package org.fzl.eventcqrsaxonapp.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;

@Getter @AllArgsConstructor
public class AccountCreatedEvent {
    private String accountId;
    private double initialBalance;
    private AccountStatus status;
    private String currency;

}
