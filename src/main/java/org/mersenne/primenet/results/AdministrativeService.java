package org.mersenne.primenet.results;

import org.mersenne.primenet.imports.Import.State;
import org.mersenne.primenet.imports.ImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class AdministrativeService {

    private final ImportRepository importRepository;

    private final ResultRepository resultRepository;

    @Autowired
    public AdministrativeService(ImportRepository importRepository, ResultRepository resultRepository) {
        this.importRepository = importRepository;
        this.resultRepository = resultRepository;
    }

    public long countResults() {
        return resultRepository.count();
    }

    public long countResultsByUserName(String userName) {
        return resultRepository.countAllByUserName(userName);
    }

    public long countImports() {
        return importRepository.count();
    }

    public Map<State, Long> countImportsPerState() {
        return importRepository.countPerState()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
