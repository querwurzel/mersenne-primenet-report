package org.mersenne.primenet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
@ConfigurationProperties(prefix = "imports")
public record PrimeNetProperties(
        @NotBlank
        String identity,
        @Past
        LocalDate start
) {
    @Override
    public LocalDate start() {
        return start == null
                ? LocalDate.now().minusDays(1)
                : start;
    }
}
