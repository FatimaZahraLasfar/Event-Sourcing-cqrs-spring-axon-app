package org.fzl.eventcqrsaxonapp.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.eventsourcing.annotation.EventTag;
import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;

@Getter @AllArgsConstructor
public class AccountCreatedEvent {
    @EventTag
    private String accountId;
    private double initialBalance;
    private AccountStatus status;
    private String currency;

}
