package org.mersenne.primenet.meta.application.model;

import org.mersenne.primenet.imports.domain.Import;

import java.time.LocalDateTime;
import java.util.Map;

public record MetaInformation(
        LocalDateTime lastUpdated,
        UserInformation user,
        ImportInformation imports,
        ResultInformation results
) {

    public record UserInformation (
            String name,
            long totalImports
    ) {}

    public record ImportInformation (
            Map<Import.State, Long> states
    ) {
        public long totalImports() {
            return this.states.values().stream().mapToLong(totalPerState -> totalPerState).sum();
        }
    }

    public record ResultInformation (
        long totalImports
    ) {}
}
