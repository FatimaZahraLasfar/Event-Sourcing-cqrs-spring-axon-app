package org.fzl.eventcqrsaxonapp.commands.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.annotation.TargetEntityId;
import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;

@Getter @AllArgsConstructor
public class UpdateAccountStatusCommand {
    @TargetEntityId
    private String id;
    AccountStatus status;
}
