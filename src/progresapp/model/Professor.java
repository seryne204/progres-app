package progresapp.model;

public class Professor {
    private final String id;
    private final String fullName;
    private final String department;
    private final String academicYear;

    public Professor(String id, String fullName, String department, String academicYear) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.academicYear = academicYear;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDepartment() {
        return department;
    }

    public String getAcademicYear() {
        return academicYear;
    }
}
