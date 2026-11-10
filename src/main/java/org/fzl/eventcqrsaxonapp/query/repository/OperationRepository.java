package org.fzl.eventcqrsaxonapp.query.repository;

import org.fzl.eventcqrsaxonapp.query.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByAccountId(String id);

}
