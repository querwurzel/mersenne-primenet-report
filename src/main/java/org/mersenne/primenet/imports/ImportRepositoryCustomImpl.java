package org.mersenne.primenet.imports;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class ImportRepositoryCustomImpl implements ImportRepositoryCustom {

    private final EntityManager entityManager;

    public ImportRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean hasImports() {
        try {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery query = cb.createQuery();
            final Root root = query.from(Import.class);
            query.select(root.get("date"));

            final TypedQuery result = entityManager.createQuery(query);
            result.setMaxResults(1);

            return Objects.nonNull(result.getSingleResult());
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean hasImportGapsSince(LocalDate inclusiveStart) {
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        return this.hasImportGapsSince(inclusiveStart, yesterday);
    }

    private boolean hasImportGapsSince(LocalDate inclusiveStart, LocalDate exclusiveEnd) {
        final long days = ChronoUnit.DAYS.between(inclusiveStart, exclusiveEnd);

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<Import> root = query.from(Import.class);

        final Predicate gteStart = cb.greaterThanOrEqualTo(
                root.get("date"), inclusiveStart);
        final Predicate ltEnd = cb.lessThan(
                root.get("date"), exclusiveEnd);

        query.select(cb.count(root));
        query.where(gteStart, ltEnd);

        final TypedQuery<Long> result = entityManager.createQuery(query);
        return result.getSingleResult() < days;
    }
}
