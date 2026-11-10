package org.fzl.eventcqrsaxonapp.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.eventsourcing.annotation.EventTag;

@Getter @AllArgsConstructor
public class AccountDebitedEvent {
    @EventTag
    private String accountId;
    private double amount;
    private String currency;

}
