package org.mersenne.primenet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
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
