package progresapp.model;

import java.io.Serializable;

public class Grade implements Serializable {
    private final String studentId;
    private final String courseId;
    private Double continuousAssessment;
    private Double exam;
    private Double resit;

    public Grade(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public Double getContinuousAssessment() {
        return continuousAssessment;
    }

    public void setContinuousAssessment(Double continuousAssessment) {
        this.continuousAssessment = continuousAssessment;
    }

    public Double getExam() {
        return exam;
    }

    public void setExam(Double exam) {
        this.exam = exam;
    }

    public Double getResit() {
        return resit;
    }

    public void setResit(Double resit) {
        this.resit = resit;
    }

    public double calculateAverageBeforeResit() {
        double cc = continuousAssessment == null ? 0.0 : continuousAssessment;
        double ex = exam == null ? 0.0 : exam;
        return (cc * 40.0 + ex * 60.0) / 100.0;
    }

    public double calculateAverageAfterResit() {
        double before = calculateAverageBeforeResit();
        if (resit == null) {
            return before;
        }
        return Math.max(before, resit);
    }

    public boolean requiresResit() {
        return calculateAverageBeforeResit() < 10.0;
    }

    public boolean isValidatedAfterResit() {
        return calculateAverageAfterResit() >= 10.0;
    }
}
