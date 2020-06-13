package org.mersenne.primenet.xml;

import javax.xml.stream.events.Attribute;
import java.time.LocalDate;
import java.util.Queue;

public final class Results {

    private String date;

    private final Queue<ResultLine> lines;

    public Results(Queue<ResultLine> lines) {
        this.lines = lines;
    }

    protected Results setDate(String date) {
        this.date = date;
        return this;
    }

    protected Results setDate(Attribute date) {
        this.setDate(date.getValue());
        return this;
    }

    public LocalDate parseDate() {
        return LocalDate.parse(date);
    }

    public String getDate() {
        return date;
    }

    public Queue<ResultLine> getLines() {
        return lines;
    }

    public boolean notEmpty() {
        return !this.lines.isEmpty();
    }

    public int size() {
        return this.lines.size();
    }

    @Override
    public String toString() {
        return "Results{" +
                "date=" + date +
                ", results=" + lines +
                '}';
    }
}
