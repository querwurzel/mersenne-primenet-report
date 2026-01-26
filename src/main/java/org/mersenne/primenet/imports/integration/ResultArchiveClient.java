package org.mersenne.primenet.imports.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ResultArchiveClient {

    private static final Logger log = LoggerFactory.getLogger(ResultArchiveClient.class);

    private static final String annualUrl = "/result_archive/%d.7z";
    private static final String dailyUrl = "/result_archive/%d/%s.xml.bz2";

    private final RestClient client;

    @Autowired
    public ResultArchiveClient(RestClient.Builder restClientBuilder) {
        this.client = restClientBuilder
                .baseUrl("https://www.mersenne.org")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
                .build();
    }

    public byte[] fetchDailyReport(LocalDate date) {
        // https://www.mersenne.org/result_archive/2019/2019-01-29.xml.bz2
        final String url = String.format(dailyUrl, date.getYear(), date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        final var archive = this.fetchDailyReport(url);
        log.debug("Fetched {} bytes for daily report of {}", archive.length, date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return archive;
    }

    public byte[] fetchDailyReport(String url) {
        log.debug("Fetching daily report from {}", url);
        return client.get()
                .uri(url)
                .retrieve()
                .body(byte[].class);
    }

    public byte[] fetchAnnualReport(LocalDate date) {
        // https://www.mersenne.org/result_archive/2018.7z
        final String url = String.format(annualUrl, date.getYear());
        final byte[] archive = this.fetchAnnualReport(url);
        log.debug("Fetched {} bytes for annual report of {}", archive.length, date.getYear());
        return archive;
    }

    public byte[] fetchAnnualReport(String url) {
        log.debug("Fetching annual report from {}", url);
        return client.get()
                .uri(url)
                .retrieve()
                .body(byte[].class);
    }
}
