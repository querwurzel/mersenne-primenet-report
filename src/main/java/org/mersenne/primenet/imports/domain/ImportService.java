package org.mersenne.primenet.imports.domain;

import org.mersenne.primenet.compression.Bzip2;
import org.mersenne.primenet.compression.SevenZip;
import org.mersenne.primenet.imports.domain.Import.State;
import org.mersenne.primenet.imports.integration.ResultArchiveClient;
import org.mersenne.primenet.results.domain.Result;
import org.mersenne.primenet.results.domain.ResultRepository;
import org.mersenne.primenet.xml.ResultLine;
import org.mersenne.primenet.xml.ResultParser;
import org.mersenne.primenet.xml.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final ImportRepository importRepository;
    private final ResultRepository resultRepository;
    private final ResultArchiveClient resultClient;
    private final ResultParser resultParser;

    @Autowired
    public ImportService(ImportRepository importRepository, ResultRepository resultRepository, ResultArchiveClient resultArchiveClient, ResultParser resultParser) {
        this.importRepository = importRepository;
        this.resultRepository = resultRepository;
        this.resultClient = resultArchiveClient;
        this.resultParser = resultParser;
    }

    @Scheduled(cron = "33 33 3 * * *")
    @Async
    void processYesterdayImport() {
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Importing results from yesterday [{}]", yesterday);
        this.importDailyResults(yesterday);
        System.gc();
    }

    @Scheduled(initialDelay = 2 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    @Async
    void processPendingImports() {
        final LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        final List<Import> imports = importRepository.findTop180ByStateAndLastAttemptBefore(State.PENDING, threshold);

        if (!imports.isEmpty()) {
            log.info("Scheduling pending {} imports", imports.size());
            imports.forEach(this::importDailyResults);
            log.info("Processed {} imports", imports.size());
            System.gc();
        }
    }

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 12 * 60 * 60 * 1000)
    @Async
    void processStaleImports() {
        final LocalDateTime threshold = LocalDateTime.now().minusHours(12);
        final List<Import> imports = importRepository.findAllByStateAndLastAttemptBefore(State.ACTIVE, threshold);

        if (!imports.isEmpty()) {
            log.warn("Resetting {} stale imports!", imports.size());
            for (Import anImport : imports) {
                anImport.reset();
                resultRepository.deleteAllByDate(anImport.getDate());
            }
            importRepository.saveAll(imports);
            System.gc();
        }
    }

    void importAnnualResults(LocalDate year) {
        try {
            final List<byte[]> archives = this.parseArchives(resultClient.fetchAnnualReport(year));
            for (byte[] archive : archives) {
                this.importDailyResults(archive);
            }
        } catch (RestClientException e) {
            log.error("Failed to fetch annual archive of {}", year.getYear(), e);
        } catch (IOException | NoSuchElementException e) {
            log.error("Failed to extract annual archive for {}", year.getYear(), e);
        }
    }

    void importDailyResults(LocalDate date) {
        this.importDailyResults(new Import(date));
    }

    private void importDailyResults(byte[] archive) {
        try {
            final Results results = this.parseResults(archive);
            final LocalDate date = results.parseDate();
            this.importDailyResults(date, results);
        } catch (IOException | XMLStreamException e) {
            log.error("Failed to parse some results of annual archive", e);
        }
    }

    private void importDailyResults(LocalDate date, Results results) {
        try {
            final Import theImport = importRepository.save(new Import(date).nextAttempt());
            this.persistImportAndResults(theImport, results);
        } catch (DataIntegrityViolationException e) {
            log.info("Import of {} already exists", date);
        }
    }

    private void importDailyResults(Import anImport) {
        try {
            final Import theImport = importRepository.save(anImport.nextAttempt());
            final byte[] archive = resultClient.fetchDailyReport(theImport.getDate());
            final Results results = this.parseResults(archive);
            this.persistImportAndResults(theImport, results);
        } catch (RestClientException e) {
            log.error("Failed to fetch daily archive of {}", anImport.getDate(), e);
            importRepository.save(anImport.failed(e.getMessage()));
        } catch (IOException | XMLStreamException e) {
            log.error("Failed to parse results of {}", anImport.getDate(), e);
            importRepository.save(anImport.failed(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            log.info("Import of {} already exists", anImport.getDate());
        } catch (IllegalStateException e) {
            log.warn("Import of {} has inconsistencies between state and attempts; resetting!", anImport.getDate());
            importRepository.save(anImport.reset());
        }
    }

    private Results parseResults(byte[] archive) throws IOException, XMLStreamException {
        try (InputStream input = Bzip2.stream(archive)) {
            return resultParser.parseResults(input);
        }
    }

    private List<byte[]> parseArchives(byte[] annualArchive) throws IOException, NoSuchElementException {
        final List<byte[]> dailyArchives = new ArrayList<>(365);
        SevenZip.extract(annualArchive).forEach(dailyArchives::add);
        return dailyArchives;
    }

    private void persistImportAndResults(Import theImport, Results result) {
        final List<Result> results = result.lines().stream()
                .map(line -> resultMapper.apply(theImport, line))
                .toList();

        resultRepository.saveAll(results);
        importRepository.save(theImport.succeeded());
        log.info("Imported {} results of {}", String.format("%1$6s", results.size()), theImport.getDate());
    }

    private static final BiFunction<Import, ResultLine, Result> resultMapper = (theImport, line) -> new Result()
            .setImport(theImport)
            .setUserName(line.getUserName())
            .setComputerName(line.getComputerName())
            .setExponent(line.getExponent())
            .setResultType(line.getResultType())
            .setAssignmentAge(line.getAssignmentAge())
            .setDate(line.parseDate())
            .setTime(line.parseTime())
            .setGhzDays(line.getGhzDays())
            .setMessage(line.getMessage());
}
