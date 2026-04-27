package progresapp.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import progresapp.model.Absence;
import progresapp.model.Course;
import progresapp.model.Grade;
import progresapp.model.ScheduleEntry;
import progresapp.model.Student;
import progresapp.model.StudentRecord;

public class DataStore {
    private final Path dataFile;

    public DataStore(Path dataFile) {
        this.dataFile = dataFile;
    }

    public List<StudentRecord> loadOrCreateDefault() {
        try {
            if (Files.notExists(dataFile.getParent())) {
                Files.createDirectories(dataFile.getParent());
            }

            if (Files.notExists(dataFile)) {
                List<StudentRecord> records = createDefaultRecords();
                saveAll(records);
                return records;
            }

            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            List<StudentRecord> records = parse(lines);
            if (records.size() < 2) {
                records = createDefaultRecords();
                saveAll(records);
            }
            return records;
        } catch (IOException exception) {
            return createDefaultRecords();
        }
    }

    public void saveAll(List<StudentRecord> records) {
        List<String> lines = new ArrayList<>();
        for (StudentRecord record : records) {
            Student student = record.getStudent();

            lines.add("[STUDENT]");
            lines.add(student.getRegistrationNumber());
            lines.add(student.getFullName());
            lines.add(student.getDepartment());
            lines.add(student.getLevel());
            lines.add(student.getAcademicYear());
            lines.add(record.getPassword());

            lines.add("[GRADES]");
            for (Grade grade : record.getGrades()) {
                Course course = grade.getCourse();
                lines.add(String.join(";",
                    course.getCode(),
                    course.getTitle(),
                    String.valueOf(course.getCoefficient()),
                    String.valueOf(course.getCredits()),
                    String.valueOf(grade.getMark())
                ));
            }

            lines.add("[SCHEDULE]");
            for (ScheduleEntry entry : record.getSchedule()) {
                lines.add(String.join(";",
                    entry.getDay(),
                    entry.getTime(),
                    entry.getModule(),
                    entry.getRoom()
                ));
            }

            lines.add("[ABSENCES]");
            for (Absence absence : record.getAbsences()) {
                lines.add(String.join(";",
                    absence.getModule(),
                    absence.getDate(),
                    absence.getReason(),
                    String.valueOf(absence.isJustified())
                ));
            }
        }

        try {
            Files.write(dataFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private List<StudentRecord> parse(List<String> lines) {
        List<StudentRecord> records = new ArrayList<>();
        int index = 0;
        while (index < lines.size()) {
            if (!"[STUDENT]".equals(lines.get(index))) {
                index++;
                continue;
            }

            if (index + 6 >= lines.size()) {
                return createDefaultRecords();
            }

            List<Grade> grades = new ArrayList<>();
            List<ScheduleEntry> schedule = new ArrayList<>();
            List<Absence> absences = new ArrayList<>();

            index++;
            Student student = new Student(
                lines.get(index++),
                lines.get(index++),
                lines.get(index++),
                lines.get(index++),
                lines.get(index++)
            );
            String password = lines.get(index++);

            String section = "";
            while (index < lines.size()) {
                String line = lines.get(index);

                if ("[STUDENT]".equals(line)) {
                    break;
                }

                index++;
                if (line.startsWith("[")) {
                    section = line;
                    continue;
                }

                String[] parts = line.split(";");
                switch (section) {
                    case "[GRADES]" -> {
                        if (parts.length == 5) {
                            Course course = new Course(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                            grades.add(new Grade(course, Double.parseDouble(parts[4])));
                        }
                    }
                    case "[SCHEDULE]" -> {
                        if (parts.length == 4) {
                            schedule.add(new ScheduleEntry(parts[0], parts[1], parts[2], parts[3]));
                        }
                    }
                    case "[ABSENCES]" -> {
                        if (parts.length == 4) {
                            absences.add(new Absence(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3])));
                        }
                    }
                    default -> {
                    }
                }
            }

            records.add(new StudentRecord(student, password, grades, schedule, absences));
        }

        return records.isEmpty() ? createDefaultRecords() : records;
    }

    private StudentRecord createFirstDefaultRecord() {
        Student student = new Student("2023-INFO-1452", "Amine Bensalem", "Informatique", "Licence 3", "2025/2026");
        List<Grade> grades = new ArrayList<>();
        grades.add(new Grade(new Course("POO301", "Programmation Orientee Objet", 3, 6), 15.5));
        grades.add(new Grade(new Course("BDD302", "Bases de donnees", 2, 4), 13.0));
        grades.add(new Grade(new Course("SE303", "Systemes d'exploitation", 2, 4), 9.5));
        grades.add(new Grade(new Course("WEB304", "Developpement Web", 2, 4), 14.0));
        grades.add(new Grade(new Course("ENG305", "Anglais technique", 1, 2), 16.0));

        List<ScheduleEntry> schedule = new ArrayList<>();
        schedule.add(new ScheduleEntry("Dimanche", "08:30 - 10:00", "Programmation Orientee Objet", "Salle 12"));
        schedule.add(new ScheduleEntry("Lundi", "10:15 - 11:45", "Bases de donnees", "Salle 07"));
        schedule.add(new ScheduleEntry("Mardi", "08:30 - 10:00", "Systemes d'exploitation", "Salle 03"));
        schedule.add(new ScheduleEntry("Mercredi", "13:00 - 14:30", "Developpement Web", "Labo 02"));
        schedule.add(new ScheduleEntry("Jeudi", "09:00 - 10:30", "Anglais technique", "Salle 18"));

        List<Absence> absences = new ArrayList<>();
        absences.add(new Absence("Systemes d'exploitation", "2026-03-18", "Absence non justifiee", false));
        absences.add(new Absence("Bases de donnees", "2026-04-02", "Certificat medical", true));

        return new StudentRecord(student, "etudiant123", grades, schedule, absences);
    }

    private StudentRecord createSecondDefaultRecord() {
        Student student = new Student("2023-INFO-1789", "Sara Meziane", "Informatique", "Licence 3", "2025/2026");

        List<Grade> grades = new ArrayList<>();
        grades.add(new Grade(new Course("POO301", "Programmation Orientee Objet", 3, 6), 12.0));
        grades.add(new Grade(new Course("BDD302", "Bases de donnees", 2, 4), 14.5));
        grades.add(new Grade(new Course("SE303", "Systemes d'exploitation", 2, 4), 11.0));
        grades.add(new Grade(new Course("WEB304", "Developpement Web", 2, 4), 16.5));
        grades.add(new Grade(new Course("ENG305", "Anglais technique", 1, 2), 15.0));

        List<ScheduleEntry> schedule = new ArrayList<>();
        schedule.add(new ScheduleEntry("Dimanche", "10:15 - 11:45", "Programmation Orientee Objet", "Salle 14"));
        schedule.add(new ScheduleEntry("Lundi", "08:30 - 10:00", "Bases de donnees", "Salle 09"));
        schedule.add(new ScheduleEntry("Mardi", "10:15 - 11:45", "Systemes d'exploitation", "Salle 05"));
        schedule.add(new ScheduleEntry("Mercredi", "14:45 - 16:15", "Developpement Web", "Labo 03"));
        schedule.add(new ScheduleEntry("Jeudi", "10:45 - 12:15", "Anglais technique", "Salle 11"));

        List<Absence> absences = new ArrayList<>();
        absences.add(new Absence("Programmation Orientee Objet", "2026-02-20", "Retard de transport", true));
        absences.add(new Absence("Developpement Web", "2026-03-27", "Absence non justifiee", false));

        return new StudentRecord(student, "sara123", grades, schedule, absences);
    }

    private List<StudentRecord> createDefaultRecords() {
        List<StudentRecord> records = new ArrayList<>();
        records.add(createFirstDefaultRecord());
        records.add(createSecondDefaultRecord());
        return records;
    }
}
