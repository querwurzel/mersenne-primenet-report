package org.mersenne.primenet.results.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mersenne.primenet.imports.domain.Import;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "results", indexes = @Index(name = "idx_user", columnList = "userName"))
public class Result implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "date", nullable = false, referencedColumnName = "date", foreignKey = @ForeignKey(name = "fk_date"))
    private Import theImport;

    @Column(name = "date", nullable = false, insertable = false, updatable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String exponent;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String computerName;
    @Column(nullable = false)
    private String resultType;
    @Column(nullable = false)
    private String ghzDays;

    private String assignmentAge;
    @Column(length = 512)
    private String message;

    @Transient
    public String getUrl() {
        return "https://www.mersenne.org/report_exponent/?full=1&exp_lo=" + this.exponent;
    }

    @Transient
    public LocalDateTime getDateReceived() {
        return LocalDateTime.of(this.date, this.time);
    }

    protected Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Result setImport(Import theImport) {
        this.theImport = theImport;
        return this;
    }

    public Result setExponent(String exponent) {
        this.exponent = exponent;
        return this;
    }

    public Result setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Result setComputerName(String computerName) {
        this.computerName = computerName;
        return this;
    }

    public Result setResultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public Result setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public Result setTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public Result setAssignmentAge(String assignmentAge) {
        this.assignmentAge = assignmentAge;
        return this;
    }

    public Result setGhzDays(String ghzDays) {
        this.ghzDays = ghzDays;
        return this;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
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

    @JsonIgnore
    public LocalDate getDate() {
        return date;
    }

    @JsonIgnore
    public LocalTime getTime() {
        return time;
    }

    public String getAssignmentAge() {
        return assignmentAge;
    }

    public String getGhzDays() {
        return ghzDays;
    }

    public String getMessage() {
        return message;
    }
}
