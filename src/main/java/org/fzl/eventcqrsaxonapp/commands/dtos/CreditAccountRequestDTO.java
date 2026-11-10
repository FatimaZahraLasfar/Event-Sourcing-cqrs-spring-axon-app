package org.fzl.eventcqrsaxonapp.commands.dtos;

public record CreditAccountRequestDTO(
        String accountId,
        double amount,
        String currency
){
}
