package org.fzl.eventcqrsaxonapp.query.dtos;

import org.fzl.eventcqrsaxonapp.query.entities.Account;
import org.fzl.eventcqrsaxonapp.query.entities.AccountOperation;

import java.util.List;

public record AccountStatementResponseDTO (
        Account account,
        List<AccountOperation> operations
){
}
