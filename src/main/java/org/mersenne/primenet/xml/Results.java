package org.mersenne.primenet.xml;

import java.time.LocalDate;
import java.util.List;

public record Results(
        String date,
        List<ResultLine> lines
) {
    public LocalDate parseDate() {
        return LocalDate.parse(date);
    }
}
