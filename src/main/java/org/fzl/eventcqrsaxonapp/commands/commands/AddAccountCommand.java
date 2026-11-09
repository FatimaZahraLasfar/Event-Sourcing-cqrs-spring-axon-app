package org.fzl.eventcqrsaxonapp.commands.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.annotation.TargetEntityId;

@Getter @AllArgsConstructor
public class AddAccountCommand {
    @TargetEntityId
    private String id;
    private double initialBalance;
    private String currency;
}
