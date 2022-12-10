package org.mersenne.primenet.imports.domain;

import org.mersenne.primenet.imports.domain.Import.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;

@Repository
public interface ImportRepository extends ImportRepositoryCustom, JpaRepository<Import, LocalDate> {

    // used for retries
    List<Import> findTop180ByStateAndLastAttemptBefore(State state, LocalDateTime lastAttempt);

    // used for bootstrapping imports
    @Query("SELECT date FROM #{#entityName}")
    List<LocalDate> findAllDates();

    // used for cleanup
    List<Import> findAllByStateAndLastAttemptBefore(State state, LocalDateTime lastAttempt);

    // used for meta
    @Query("SELECT state AS key, COUNT(state) AS value FROM #{#entityName} GROUP BY state")
    List<Entry<State, Long>> countPerState();

}
