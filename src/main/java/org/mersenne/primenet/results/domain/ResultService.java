package org.mersenne.primenet.results.domain;

import org.mersenne.primenet.PrimeNetProperties;
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
        this.identity = primeNetProperties.getIdentity();
    }

    public List<Result> fetchRecentResults() {
        return resultRepository.findTop10ByUserNameOrderByDateDesc(this.identity);
    }
}
