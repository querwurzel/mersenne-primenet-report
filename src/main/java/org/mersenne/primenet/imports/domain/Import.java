package org.mersenne.primenet.imports.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "imports", indexes = @Index(name = "idx_state", columnList = "state"))
public class Import implements Serializable {

    private static final int MAX_ATTEMPTS = 3;

    @Id
    private LocalDate date = LocalDate.now();

    @Column(nullable = false)
    private Integer attempts = 0;
    private LocalDateTime lastAttempt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;
    private String reason;

    private Import() {}

    public Import(LocalDate date) {
        this.date = Objects.requireNonNull(date);
    }

    public Import reset() {
        this.attempts = 0;
        this.lastAttempt = null;
        this.reason = null;
        this.state = State.PENDING;
        return this;
    }

    public boolean hasNextAttempt() {
        return this.attempts < MAX_ATTEMPTS;
    }

    public Import nextAttempt() {
        if (this.hasNextAttempt()) {
            this.lastAttempt = LocalDateTime.now();
            this.state = State.ACTIVE;
            return this;
        }

        throw new IllegalStateException("Import has reached max attempts");
    }

    public Import succeeded() {
        return this.handleSuccess();
    }

    @SuppressWarnings("unused")
    public Import failed() {
        return this.handleNonSuccess(null);
    }

    public Import failed(String reason) {
        return this.handleNonSuccess(reason);
    }

    private Import handleSuccess() {
        this.reason = null;
        this.attempts++;
        this.state = State.SUCCESS;
        return this;
    }

    private Import handleNonSuccess(String reason) {
        this.reason = reason;
        this.attempts++;
        this.state = this.hasNextAttempt()
                ? State.PENDING
                : State.FAILURE;

        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    protected Import setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public Integer getAttempts() {
        return attempts;
    }

    protected Import setAttempts(Integer attempts) {
        this.attempts = attempts;
        return this;
    }

    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }

    protected Import setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
        return this;
    }

    public State getState() {
        return state;
    }

    protected Import setState(State state) {
        this.state = state;
        return this;
    }

    public String getReason() {
        return reason;
    }

    protected Import setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public enum State {
        PENDING,
        ACTIVE,
        FAILURE,
        SUCCESS;
    }
}
