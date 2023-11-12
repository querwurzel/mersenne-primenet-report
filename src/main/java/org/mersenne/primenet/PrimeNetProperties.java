package org.mersenne.primenet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
@ConfigurationProperties(prefix = "imports")
public class PrimeNetProperties {

    @NotNull
    @NotBlank
    private String identity;
    @NotNull
    @Past
    private LocalDate start;

    public String getIdentity() {
        return identity;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setIdentity(String identity) {
        this.identity = StringUtils.hasText(identity)
                ? identity
                : "ANONYMOUS";
    }

    /**
     * PrimeNet result archives exists as of 1997-11-11.
     */
    public void setStart(String start) {
        this.start = StringUtils.hasText(start)
                ? LocalDate.parse(start)
                : LocalDate.now().minusDays(1);
    }
}
