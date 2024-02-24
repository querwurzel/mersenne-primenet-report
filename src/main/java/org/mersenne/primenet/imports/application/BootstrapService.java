package org.mersenne.primenet.imports.application;

import org.mersenne.primenet.PrimeNetProperties;
import org.mersenne.primenet.imports.domain.ImportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
class BootstrapService {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    private final ImportRepository importRepository;
    private final ImportService importService;
    private final LocalDate importStart;

    @Autowired
    public BootstrapService(ImportRepository importRepository, ImportService importService, PrimeNetProperties primeNetProperties) {
        this.importRepository = importRepository;
        this.importService = importService;
        this.importStart = primeNetProperties.start();
    }

    @Bean
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
        final SortedSet<LocalDate> years = calculateAnnualImports(importStart);
        years.forEach(year -> {
            log.info("Importing annual results for year {}", year.getYear());
            importService.importAnnualResults(year);
            log.info("Imported annual results for year {}", year.getYear());
        });
    }

    private void bootstrapDailyImports() {
        final SortedSet<LocalDate> days = calculateDailyImports(importStart);
        days.removeAll(importRepository.findAllDates());

        if (!days.isEmpty()) {
            log.info("Importing {} daily results as of {}", days.size(), importStart);
            days.forEach(importService::importDailyResults);
            log.info("Imported {} daily results", days.size());
        }
    }

    private static SortedSet<LocalDate> calculateAnnualImports(LocalDate inclusiveStart) {
        final SortedSet<LocalDate> missing = new TreeSet<>();

        for (LocalDate year = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
             !inclusiveStart.isAfter(year);
             year = year.minusYears(1)
        ) {
            missing.add(year);
        }

        return missing;
    }

    private static SortedSet<LocalDate> calculateDailyImports(LocalDate inclusiveStart) {
        final SortedSet<LocalDate> missing = new TreeSet<>();

        for (LocalDate day = LocalDate.now().minusDays(1);
             !inclusiveStart.isAfter(day);
             day = day.minusDays(1)
        ) {
            missing.add(day);
        }

        return missing;
    }
}
