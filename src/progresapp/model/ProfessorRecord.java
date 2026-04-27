package progresapp.model;

import java.util.List;

public class ProfessorRecord {
    private final Professor professor;
    private final String password;
    private final List<String> sections;
    private final List<ProfessorScheduleEntry> schedule;
    private final List<String> taughtCourseCodes;

    public ProfessorRecord(
        Professor professor,
        String password,
        List<String> sections,
        List<ProfessorScheduleEntry> schedule,
        List<String> taughtCourseCodes
    ) {
        this.professor = professor;
        this.password = password;
        this.sections = sections;
        this.schedule = schedule;
        this.taughtCourseCodes = taughtCourseCodes;
    }

    public Professor getProfessor() {
        return professor;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getSections() {
        return sections;
    }

    public List<ProfessorScheduleEntry> getSchedule() {
        return schedule;
    }

    public List<String> getTaughtCourseCodes() {
        return taughtCourseCodes;
    }
}
