package org.mersenne.primenet.imports.domain;

import java.time.LocalDate;

interface ImportRepositoryCustom {

    boolean hasImports();

    boolean hasImportGapsSince(LocalDate inclusiveStart);

}
