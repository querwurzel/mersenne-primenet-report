package org.mersenne.primenet.results;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    @Autowired
    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public List<Result> fetchRecentResultsByUser(String user) {
        return resultRepository.findTop10ByUserNameOrderByDateDesc(user);
    }
}
