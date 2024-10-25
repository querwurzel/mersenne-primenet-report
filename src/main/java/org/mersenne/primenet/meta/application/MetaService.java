package org.mersenne.primenet.meta.application;

import org.mersenne.primenet.PrimeNetProperties;
import org.mersenne.primenet.imports.domain.Import.State;
import org.mersenne.primenet.imports.domain.ImportRepository;
import org.mersenne.primenet.meta.application.model.MetaInformation;
import org.mersenne.primenet.meta.application.model.MetaInformation.ImportInformation;
import org.mersenne.primenet.meta.application.model.MetaInformation.ResultInformation;
import org.mersenne.primenet.meta.application.model.MetaInformation.UserInformation;
import org.mersenne.primenet.imports.domain.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class MetaService {

    private static final Logger log = LoggerFactory.getLogger(MetaService.class);

    private final ImportRepository importRepository;

    private final ResultRepository resultRepository;

    private final AtomicReference<MetaInformation> meta;

    private final String identity;

    @Autowired
    public MetaService(
            final ImportRepository importRepository,
            final ResultRepository resultRepository,
            final PrimeNetProperties primeNetProperties
    ) {
        this.importRepository = importRepository;
        this.resultRepository = resultRepository;
        this.identity = primeNetProperties.identity();
        this.meta = new AtomicReference<>();
    }

    @Scheduled(initialDelay = 1, fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
    private void refreshMetaInformation() {
        this.meta.set(new MetaInformation(
                LocalDateTime.now(),
                new UserInformation(identity, countResultsByUserName(identity)),
                new ImportInformation(countImportsPerState()),
                new ResultInformation(countResults())
        ));
        log.debug("Updated meta information");
    }

    public Optional<MetaInformation> getMeta() {
        return Optional.ofNullable(this.meta.get());
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
}
