package org.mersenne.primenet.results;

import org.mersenne.primenet.PrimeNetProperties;
import org.mersenne.primenet.imports.Import.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/results/meta")
public class AdministrativeController {

    private final AdministrativeService administrativeService;

    private final AtomicReference<Meta> meta;

    private final String identity;

    @Autowired
    public AdministrativeController(AdministrativeService administrativeService, PrimeNetProperties primeNetProperties) {
        this.administrativeService = administrativeService;
        this.identity = primeNetProperties.getIdentity();
        this.meta = new AtomicReference<>(new Meta(this.identity));
    }

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 60 * 1000)
    protected void refreshMeta() {
        this.meta.set(new Meta()
                .setUser(identity)
                .setUserResults(administrativeService.countResultsByUserName(identity))
                .setResults(administrativeService.countResults())
                .setImportStates(administrativeService.countImportsPerState())
                .setUser(identity)
                .setUserResults(administrativeService.countResultsByUserName(identity)));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected Meta getMetadata() {
        return this.meta.get();
    }

    protected class Meta {

        public final LocalDateTime lastUpdated = LocalDateTime.now();

        public final UserMeta user = new UserMeta();

        public final ImportMeta imports = new ImportMeta();

        public final ResultMeta results = new ResultMeta();

        public Meta() {}

        public Meta(String user) {
            this.setUser(user);
        }

        public Meta setUser(String user) {
            this.user.setName(user);
            return this;
        }

        public Meta setUserResults(long results) {
            this.user.setTotal(results);
            return this;
        }

        public Meta setImportStates(Map<State, Long> states) {
            this.imports.setStates(states);
            return this;
        }

        public Meta setResults(long total) {
            this.results.setTotal(total);
            return this;
        }
    }

    protected class UserMeta {

        public String name;
        public long total = 0;

        public UserMeta() {}

        public UserMeta setTotal(long total) {
            this.total = total;
            return this;
        }

        public UserMeta setName(String name) {
            this.name = name;
            return this;
        }
    }

    protected class ImportMeta {

        public Map<State, Long> states;

        public ImportMeta() {
            this.states = Collections.emptyMap();
        }

        public ImportMeta setStates(Map<State, Long> states) {
            this.states = Objects.requireNonNull(states);
            return this;
        }

        public long getTotal() {
            return this.states.values().stream().mapToLong(value -> value).sum();
        }
    }

    protected class ResultMeta {

        public long total = 0;

        public ResultMeta setTotal(long results) {
            this.total = results;
            return this;
        }
    }
}
