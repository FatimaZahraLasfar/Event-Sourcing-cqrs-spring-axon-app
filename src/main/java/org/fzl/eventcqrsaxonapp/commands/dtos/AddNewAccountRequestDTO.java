package org.fzl.eventcqrsaxonapp.commands.dtos;

public record AddNewAccountRequestDTO (
        double initialBalance,
        String currency
){
}
