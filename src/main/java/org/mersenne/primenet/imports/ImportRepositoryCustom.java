package org.mersenne.primenet.imports;

import java.time.LocalDate;

public interface ImportRepositoryCustom {

    boolean hasImports();

    boolean hasImportGapsSince(LocalDate inclusiveStart);

}
