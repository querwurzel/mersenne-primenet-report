package org.mersenne.primenet.imports;

import org.mersenne.primenet.PrimeNetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Service
public class BootstrapService {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    private final ImportRepository importRepository;

    private final ImportService importService;

    private final LocalDate importStart;

    @Autowired
    public BootstrapService(ImportRepository importRepository, ImportService importService, PrimeNetProperties primeNetProperties) {
        this.importRepository = importRepository;
        this.importService = importService;
        this.importStart = primeNetProperties.getStart();
    }

    @Bean
    @Lazy
    protected ApplicationRunner importBootstrapper() {
        return new ApplicationRunner() {
            @Async
            @Override
            public void run(ApplicationArguments args) {
                if (importRepository.hasImports()) {
                    if (importRepository.hasImportGapsSince(importStart)) {
                        BootstrapService.this.bootstrapDailyImports();
                    }
                } else {
                    BootstrapService.this.bootstrapAnnualImports();
                    BootstrapService.this.bootstrapDailyImports();
                }
                log.info("Bootstrapping complete");
                System.gc();
            }
        };
    }

    private void bootstrapAnnualImports() {
        final Set<LocalDate> years = this.selectAnnualImports(importStart);
        years.forEach(year -> {
            log.info("Importing annual results for year {}", year.getYear());
            importService.importAnnualResults(year);
            log.info("Imported annual results for year {}", year.getYear());
        });
    }

    private void bootstrapDailyImports() {
        final Set<LocalDate> days = this.selectDailyImports(importStart);
        if (!days.isEmpty()) {
            log.info("Importing {} daily results as of {}", days.size(), importStart);
            days.forEach(importService::importDailyResults);
            log.info("Imported {} daily results", days.size());
        }
    }

    private Set<LocalDate> selectAnnualImports(LocalDate inclusiveStart) {
        final Set<LocalDate> missing = new TreeSet<>();

        for (LocalDate year = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
             !inclusiveStart.isAfter(year);
             year = year.minusYears(1)
        ) {
            missing.add(year);
        }

        return Collections.unmodifiableSet(missing);
    }

    private Set<LocalDate> selectDailyImports(LocalDate inclusiveStart) {
        final Set<LocalDate> missing = new TreeSet<>();

        for (LocalDate day = LocalDate.now().minusDays(1);
             !inclusiveStart.isAfter(day);
             day = day.minusDays(1)
        ) {
            missing.add(day);
        }

        missing.removeAll(importRepository.findAllDates());
        return Collections.unmodifiableSet(missing);
    }
}
