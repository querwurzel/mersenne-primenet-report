package org.mersenne.primenet.imports;

import org.mersenne.primenet.compress.Bzip2;
import org.mersenne.primenet.compress.SevenZip;
import org.mersenne.primenet.api.ResultArchiveClient;
import org.mersenne.primenet.imports.Import.State;
import org.mersenne.primenet.results.Result;
import org.mersenne.primenet.results.ResultRepository;
import org.mersenne.primenet.xml.ResultLine;
import org.mersenne.primenet.xml.ResultParser;
import org.mersenne.primenet.xml.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    protected void processYesterdayImport() {
        final LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Importing results from yesterday [{}]", yesterday);
        this.importDailyResults(yesterday);
        System.gc();
    }

    @Scheduled(initialDelay = 2 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    @Async
    protected void processPendingImports() {
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
    protected void processStaleImports() {
        final LocalDateTime threshold = LocalDateTime.now().minusHours(12);
        final List<Import> imports = importRepository.findAllByStateAndLastAttemptBefore(State.ACTIVE, threshold);

        if (!imports.isEmpty()) {
            log.warn("Resetting {} stale imports!", imports.size());
            imports.forEach(theImport -> {
                theImport.reset();
                resultRepository.deleteAllByDate(theImport.getDate());
            });
            importRepository.saveAll(imports);
            System.gc();
        }
    }

    protected void importAnnualResults(LocalDate year) {
        try {
            final List<byte[]> archives = this.parseArchives(resultClient.fetchAnnualReport(year));
            archives.forEach(this::importDailyResults);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.error("Failed to fetch result archive of {}, HTTP {}", year.getYear(), e.getStatusCode());
            } else {
                log.error("Failed to fetch annual archive of {}", year.getYear(), e);
            }
        } catch (IOException | NoSuchElementException e) {
            log.error("Failed to extract annual archive for {}", year.getYear(), e);
        }
    }

    protected void importDailyResults(LocalDate date) {
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
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.error("Failed to fetch daily archive of {}, HTTP {}", anImport.getDate(), e.getStatusCode());
            } else {
                log.error("Failed to fetch daily archive of {}", anImport.getDate(), e);
            }
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
        final List<byte[]> archives = new ArrayList<>(365);
        SevenZip.extract(annualArchive).forEach(archives::add);
        return archives;
    }

    private void persistImportAndResults(Import theImport, Results result) {
        final int count = result.size();
        final int chunkSize = 10_000;
        final List<Result> results = new ArrayList<>(chunkSize);

        for (final Iterator<ResultLine> it = result.getLines().iterator(); it.hasNext(); it.remove()) {
            results.add(resultMapper.apply(theImport, it.next()));

            if (results.size() >= chunkSize) {
                resultRepository.saveAll(results);
                results.clear();
            }
        }

        resultRepository.saveAll(results);
        importRepository.save(theImport.succeeded());
        log.info("Imported {} results of {}", String.format("%1$6s", count), theImport.getDate());
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
