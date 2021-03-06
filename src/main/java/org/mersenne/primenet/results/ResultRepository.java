package org.mersenne.primenet.results;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findTop10ByUserNameOrderByDateDesc(String userName);

    Long countAllByUserName(String userName);

    @Modifying
    @Transactional
    Long deleteAllByDate(LocalDate date);

}
