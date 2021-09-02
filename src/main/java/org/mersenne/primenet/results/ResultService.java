package org.mersenne.primenet.results;

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

    public List<Result> fetchRecentResultsByUser() {
        return this.fetchRecentResultsByUser(identity);
    }

    public List<Result> fetchRecentResultsByUser(String user) {
        return resultRepository.findTop10ByUserNameOrderByDateDesc(user);
    }
}
