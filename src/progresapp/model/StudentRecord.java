package progresapp.model;

import java.util.List;

public class StudentRecord {
    private final Student student;
    private final String password;
    private final List<Grade> grades;
    private final List<ScheduleEntry> schedule;
    private final List<Absence> absences;

    public StudentRecord(
        Student student,
        String password,
        List<Grade> grades,
        List<ScheduleEntry> schedule,
        List<Absence> absences
    ) {
        this.student = student;
        this.password = password;
        this.grades = grades;
        this.schedule = schedule;
        this.absences = absences;
    }

    public Student getStudent() {
        return student;
    }

    public String getPassword() {
        return password;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public List<ScheduleEntry> getSchedule() {
        return schedule;
    }

    public List<Absence> getAbsences() {
        return absences;
    }
}
