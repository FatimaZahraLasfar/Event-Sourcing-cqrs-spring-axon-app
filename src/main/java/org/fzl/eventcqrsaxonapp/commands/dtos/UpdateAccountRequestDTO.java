package org.fzl.eventcqrsaxonapp.commands.dtos;

import org.fzl.eventcqrsaxonapp.commands.enums.AccountStatus;

public record UpdateAccountRequestDTO(
        String accountId,
        AccountStatus status
){
}
