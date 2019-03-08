package org.mersenne.primenet.xml;

import javax.xml.stream.events.Attribute;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public final class Results {

    private String date;

    private List<ResultLine> results;

    public Results () {}

    public Results(List<ResultLine> results) {
        this.results = results;
    }

    public String getDate() {
        return date;
    }

    public LocalDate parseDate() {
        return LocalDate.parse(date);
    }

    protected Results setDate(String date) {
        this.date = date;
        return this;
    }

    protected Results setDate(Attribute date) {
        this.date = date.getValue();
        return this;
    }

    public List<ResultLine> getResults() {
        return Collections.unmodifiableList(results);
    }

    public boolean notEmpty() {
        return !this.results.isEmpty();
    }

    @Override
    public String toString() {
        return "Results{" +
                "date=" + date +
                ", results=" + results +
                '}';
    }
}
