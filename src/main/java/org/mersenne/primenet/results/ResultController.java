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

import java.util.List;

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
    protected ResponseEntity<List<Result>> getMyRecentResults() {
        return this.getRecentResultsByUser(identity);
    }

    protected ResponseEntity<List<Result>> getRecentResultsByUser(String user) {
        final List<Result> results = resultService.fetchRecentResultsByUser(user);
        return ResponseEntity
                .status(results.isEmpty()
                        ? HttpStatus.NOT_FOUND
                        : HttpStatus.OK)
                .body(results);
    }
}
