package org.mersenne.primenet.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ResultArchiveClient {

    private static final Logger log = LoggerFactory.getLogger(ResultArchiveClient.class);

    private static final String annualUrl = "https://www.mersenne.org/result_archive/%d.7z";
    private static final String dailyUrl  = "https://www.mersenne.org/result_archive/%d/%s.xml.bz2";

    private final RestTemplate restTemplate;

    @Autowired
    public ResultArchiveClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public byte[] fetchDailyReport(LocalDate date) {
        // https://www.mersenne.org/result_archive/2019/2019-01-29.xml.bz2
        final String url = String.format(dailyUrl, date.getYear(), date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return this.fetchDailyReport(url);
    }

    public byte[] fetchDailyReport(String url) {
        log.debug("Fetching daily report using url {}", url);
        return restTemplate.getForObject(url, byte[].class);
    }

    public byte[] fetchAnnualReport(LocalDate date) {
        // https://www.mersenne.org/result_archive/2018.7z
        final String url = String.format(annualUrl, date.getYear());
        return this.fetchAnnualReport(url);
    }

    public byte[] fetchAnnualReport(String url) {
        log.debug("Fetching annual report using url {}", url);
        return restTemplate.getForObject(url, byte[].class);
    }
}
