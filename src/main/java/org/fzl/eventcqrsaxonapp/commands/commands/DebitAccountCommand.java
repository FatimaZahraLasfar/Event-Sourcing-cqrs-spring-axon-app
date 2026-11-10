package org.fzl.eventcqrsaxonapp.commands.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.annotation.TargetEntityId;

@Getter @AllArgsConstructor
public class DebitAccountCommand {
    @TargetEntityId
    private String id;
    private double amount;
    private String currency;
}
