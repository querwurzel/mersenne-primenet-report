package org.mersenne.primenet.imports.application;

import org.mersenne.primenet.PrimeNetProperties;
import org.mersenne.primenet.imports.domain.Result;
import org.mersenne.primenet.imports.domain.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    private final String identity;

    @Autowired
    public ResultService(ResultRepository resultRepository, PrimeNetProperties primeNetProperties) {
        this.resultRepository = resultRepository;
        this.identity = primeNetProperties.identity();
    }

    public List<Result> fetchRecentResults() {
        return resultRepository.findTop10ByUserNameOrderByDateDesc(this.identity);
    }
}
