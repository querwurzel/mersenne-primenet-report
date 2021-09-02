package org.mersenne.primenet.meta;

import graphql.kickstart.tools.GraphQLResolver;
import org.mersenne.primenet.PrimeNetProperties;
import org.mersenne.primenet.imports.Import.State;
import org.mersenne.primenet.imports.ImportRepository;
import org.mersenne.primenet.meta.MetaService.Meta;
import org.mersenne.primenet.results.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class MetaService implements GraphQLResolver<Meta> {

    private final ImportRepository importRepository;

    private final ResultRepository resultRepository;

    private final AtomicReference<Meta> meta;

    private final String identity;

    @Autowired
    public MetaService(ImportRepository importRepository, ResultRepository resultRepository, PrimeNetProperties primeNetProperties) {
        this.importRepository = importRepository;
        this.resultRepository = resultRepository;
        this.identity = primeNetProperties.getIdentity();
        this.meta = new AtomicReference<>(new Meta(this.identity));
    }
    
    @Scheduled(initialDelay = 5 * 60 * 1000, fixedRate = 60 * 60 * 1000)
    protected void refreshMeta() {
        this.meta.set(new Meta()
                .setUser(identity)
                .setUserResults(countResultsByUserName(identity))
                .setResults(countResults())
                .setImportStates(countImportsPerState()));
    }

    public Meta getMeta() {
        return this.meta.get();
    }

    public long countResults() {
        return resultRepository.count();
    }

    public long countResultsByUserName(String userName) {
        return resultRepository.countAllByUserName(userName);
    }

    public Map<State, Long> countImportsPerState() {
        return importRepository.countPerState()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }



    public static class Meta {

        public final String lastUpdated = LocalDateTime.now().toString();

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

    public static class UserMeta {

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

    public static class ImportMeta {

        public List<Import> states;

        public ImportMeta() {
            this.states = Collections.emptyList();
        }

        public ImportMeta(Map<State, Long> states) {
            this.setStates(states);
        }

        public ImportMeta setStates(Map<State, Long> states) {
            this.states = Objects.requireNonNull(states.entrySet().stream()
                    .map(entry -> new Import(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList()));
            return this;
        }

        public long getTotal() {
            return this.states.stream().mapToLong(value -> value.total).sum();
        }
    }

    public static class Import {

        public final State state;

        public final Long total;

        public Import(State state, Long total) {
            this.state = state;
            this.total = total;
        }
    }

    public static class ResultMeta {

        public long total = 0;

        public ResultMeta setTotal(long results) {
            this.total = results;
            return this;
        }
    }
}
