package org.fzl.eventcqrsaxonapp.commands.dtos;

public record DebitAccountRequestDTO(
        String accountId,
        double amount,
        String currency
){
}
