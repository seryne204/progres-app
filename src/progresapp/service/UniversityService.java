package progresapp.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import progresapp.model.Absence;
import progresapp.model.Grade;
import progresapp.model.Professor;
import progresapp.model.ProfessorRecord;
import progresapp.model.ProfessorScheduleEntry;
import progresapp.model.ScheduleEntry;
import progresapp.model.Student;
import progresapp.model.StudentRecord;

public class UniversityService {
    private final DataStore dataStore;
    private final List<StudentRecord> studentRecords;
    private StudentRecord currentStudentRecord;
    private final ProfessorRecord professorRecord;

    public UniversityService() {
        this.dataStore = new DataStore(Path.of("data", "student-data.txt"));
        this.studentRecords = new ArrayList<>(dataStore.loadOrCreateDefault());
        this.currentStudentRecord = studentRecords.get(0);
        this.professorRecord = createDefaultProfessorRecord();
    }

    public boolean authenticate(String registrationNumber, String password) {
        for (StudentRecord record : studentRecords) {
            Student student = record.getStudent();
            if (student.getRegistrationNumber().equals(registrationNumber) && record.getPassword().equals(password)) {
                currentStudentRecord = record;
                return true;
            }
        }
        return false;
    }

    public boolean authenticateProfessor(String id, String password) {
        Professor professor = professorRecord.getProfessor();
        return professor.getId().equals(id) && professorRecord.getPassword().equals(password);
    }

    public Student getStudent() {
        return currentStudentRecord.getStudent();
    }

    public Professor getProfessor() {
        return professorRecord.getProfessor();
    }

    public List<Grade> getGrades() {
        return currentStudentRecord.getGrades();
    }

    public List<ScheduleEntry> getSchedule() {
        return currentStudentRecord.getSchedule();
    }

    public List<Absence> getAbsences() {
        return currentStudentRecord.getAbsences();
    }

    public List<StudentRecord> getStudentRecords() {
        return studentRecords;
    }

    public List<String> getProfessorSections() {
        return professorRecord.getSections();
    }

    public List<ProfessorScheduleEntry> getProfessorSchedule() {
        return professorRecord.getSchedule();
    }

    public List<String> getProfessorTaughtCourseCodes() {
        return professorRecord.getTaughtCourseCodes();
    }

    public void save() {
        dataStore.saveAll(studentRecords);
    }

    public boolean updateStudentGrade(String registrationNumber, String courseCode, double newMark) {
        if (!professorRecord.getTaughtCourseCodes().contains(courseCode.toUpperCase())) {
            return false;
        }

        for (StudentRecord record : studentRecords) {
            if (!record.getStudent().getRegistrationNumber().equals(registrationNumber)) {
                continue;
            }

            for (int index = 0; index < record.getGrades().size(); index++) {
                Grade grade = record.getGrades().get(index);
                if (grade.getCourse().getCode().equalsIgnoreCase(courseCode)) {
                    record.getGrades().set(index, new Grade(grade.getCourse(), newMark));
                    save();
                    return true;
                }
            }
        }
        return false;
    }

    public double calculateAverage() {
        double weightedSum = 0.0;
        int coefficientSum = 0;

        for (Grade grade : getGrades()) {
            weightedSum += grade.getMark() * grade.getCourse().getCoefficient();
            coefficientSum += grade.getCourse().getCoefficient();
        }

        return coefficientSum == 0 ? 0.0 : weightedSum / coefficientSum;
    }

    public int calculateValidatedCredits() {
        int credits = 0;

        for (Grade grade : getGrades()) {
            if (grade.getMark() >= 10.0) {
                credits += grade.getCourse().getCredits();
            }
        }

        return credits;
    }

    private ProfessorRecord createDefaultProfessorRecord() {
        Professor professor = new Professor("PROF-INFO-01", "Dr Samir Khellaf", "Informatique", "2025/2026");

        List<String> sections = List.of("L3 Informatique - Section A", "L3 Informatique - Section B", "L2 Informatique - Section C");
        List<ProfessorScheduleEntry> schedule = List.of(
            new ProfessorScheduleEntry("Dimanche", "08:30 - 10:00", "Programmation Orientee Objet", "Section A", "Salle 12"),
            new ProfessorScheduleEntry("Lundi", "10:15 - 11:45", "Bases de donnees", "Section B", "Salle 07"),
            new ProfessorScheduleEntry("Mercredi", "13:00 - 14:30", "Developpement Web", "Section C", "Labo 02")
        );
        List<String> taughtCourseCodes = List.of("POO301", "BDD302", "WEB304");

        return new ProfessorRecord(professor, "prof123", sections, schedule, taughtCourseCodes);
    }
}
