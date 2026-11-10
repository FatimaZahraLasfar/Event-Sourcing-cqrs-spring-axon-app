package org.fzl.eventcqrsaxonapp.query.repository;

import org.fzl.eventcqrsaxonapp.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
