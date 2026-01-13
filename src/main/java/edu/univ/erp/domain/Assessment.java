// Domain model representing an assessment component with weight
package edu.univ.erp.domain;

public class Assessment {
    private int assessmentId;
    private int sectionId;
    private String componentName;
    private int weight;

    public Assessment() {
    }

    public Assessment(int assessmentId, int sectionId, String componentName, int weight) {
        this.assessmentId = assessmentId;
        this.sectionId = sectionId;
        this.componentName = componentName;
        this.weight = weight;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
