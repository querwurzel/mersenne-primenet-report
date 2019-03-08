package org.mersenne.primenet.results;

import org.mersenne.primenet.PrimeNetProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping({"/results", "/"})
public class ResultController {

    private final ResultService resultService;

    private final String identity;

    @Autowired
    public ResultController(ResultService resultService, PrimeNetProperties primeNetProperties) {
        this.resultService = resultService;
        this.identity = primeNetProperties.getIdentity();
    }

    @CrossOrigin
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<Results> getMyRecentResults() {
        return this.getRecentResultsByUser(identity);
    }

    protected ResponseEntity<Results> getRecentResultsByUser(String user) {
        final Results results = new Results(user, resultService.fetchRecentResultsByUser(user));
        return ResponseEntity
                .status(results.isEmpty()
                        ? HttpStatus.NOT_FOUND
                        : HttpStatus.OK)
                .body(results);
    }

    protected static class Results {

        public final String userName;

        public final List<Result> results;

        public Results(String userName, List<Result> results) {
            this.userName = Objects.requireNonNull(userName);
            this.results = Objects.isNull(results)
                    ? Collections.emptyList()
                    : results;
        }

        private boolean isEmpty() {
            return this.results.isEmpty();
        }
    }
}
