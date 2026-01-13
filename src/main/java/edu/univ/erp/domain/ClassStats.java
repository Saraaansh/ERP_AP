// Domain model for class statistics including counts and GPA
package edu.univ.erp.domain;

public class ClassStats {
    private int total;
    private double average;
    private double highest;
    private double lowest;

    public ClassStats(int total, double average, double highest, double lowest) {
        this.total = total;
        this.average = average;
        this.highest = highest;
        this.lowest = lowest;
    }

    public int getTotal() { return total; }
    public double getAverage() { return average; }
    public double getHighest() { return highest; }
    public double getLowest() { return lowest; }
}

