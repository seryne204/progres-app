package progresapp.model;

public class Student {
    private final String registrationNumber;
    private final String fullName;
    private final String department;
    private final String level;
    private final String academicYear;

    public Student(String registrationNumber, String fullName, String department, String level, String academicYear) {
        this.registrationNumber = registrationNumber;
        this.fullName = fullName;
        this.department = department;
        this.level = level;
        this.academicYear = academicYear;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDepartment() {
        return department;
    }

    public String getLevel() {
        return level;
    }

    public String getAcademicYear() {
        return academicYear;
    }
}
