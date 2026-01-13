// Domain model representing an academic term
package edu.univ.erp.domain;

import java.time.LocalDate;

public class Term {
    private int termId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dropDeadline;

    public Term() {
    }

    public Term(int termId, String name, LocalDate startDate, LocalDate endDate, LocalDate dropDeadline) {
        this.termId = termId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dropDeadline = dropDeadline;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getDropDeadline() {
        return dropDeadline;
    }

    public void setDropDeadline(LocalDate dropDeadline) {
        this.dropDeadline = dropDeadline;
    }
}
