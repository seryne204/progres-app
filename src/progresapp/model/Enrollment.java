package progresapp.model;

import java.io.Serializable;

public class Enrollment implements Serializable {
    private final String studentId;
    private final String courseId;
    private final String enrollmentDate;

    public Enrollment(String studentId, String courseId, String enrollmentDate) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }
}
