package org.mersenne.primenet.xml;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public final class ResultLine {

    private static final List<DateTimeFormatter> knownFormats = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    );

    private String exponent;
    private String userName;
    private String computerName;
    private String resultType;
    private String ghzDays;
    private String dateReceived;
    private String assignmentAge;
    private String message;

    public LocalDate parseDate() {
        for (DateTimeFormatter format : knownFormats) {
            try {
                return LocalDate.parse(this.dateReceived, format);
            } catch (DateTimeParseException ignored) {}
        }

        throw new DateTimeParseException("Unknown format", this.dateReceived, 0);
    }

    public LocalTime parseTime() {
        for (DateTimeFormatter format : knownFormats) {
            try {
                return LocalTime.parse(this.dateReceived, format);
            } catch (DateTimeParseException ignored) {}
        }

        throw new DateTimeParseException("Unknown format", this.dateReceived, 0);
    }

    public String getExponent() {
        return exponent;
    }

    public String getUserName() {
        return userName;
    }

    public String getComputerName() {
        return computerName;
    }

    public String getResultType() {
        return resultType;
    }

    public String getGhzDays() {
        return ghzDays;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public String getAssignmentAge() {
        return assignmentAge;
    }

    public String getMessage() {
        return message;
    }

    void setExponent(String exponent) {
        this.exponent = exponent;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    void setResultType(String resultType) {
        this.resultType = resultType;
    }

    void setGhzDays(String ghzDays) {
        this.ghzDays = ghzDays;
    }

    void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    void setAssignmentAge(String assignmentAge) {
        this.assignmentAge = assignmentAge;
    }

    void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResultLine{" +
                "exponent='" + exponent + '\'' +
                ", userName='" + userName + '\'' +
                ", computerName='" + computerName + '\'' +
                ", resultType='" + resultType + '\'' +
                ", ghzDays='" + ghzDays + '\'' +
                ", dateReceived='" + dateReceived + '\'' +
                ", assignmentAge='" + assignmentAge + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
