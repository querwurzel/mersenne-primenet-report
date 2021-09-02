package org.mersenne.primenet.results;

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

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @CrossOrigin
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<List<Result>> getMyRecentResults() {
        final List<Result> results = resultService.fetchRecentResults();
        return ResponseEntity
                .status(results.isEmpty()
                        ? HttpStatus.NO_CONTENT
                        : HttpStatus.OK)
                .body(results);
    }
}
