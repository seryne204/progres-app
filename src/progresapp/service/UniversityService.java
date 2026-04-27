package progresapp.service;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import progresapp.model.Administrator;
import progresapp.model.Course;
import progresapp.model.Enrollment;
import progresapp.model.Grade;
import progresapp.model.Professor;
import progresapp.model.Student;

public class UniversityService {
    private final DataStore dataStore;
    private UniversityData data;
    private Student currentStudent;
    private Professor currentProfessor;
    private Administrator currentAdministrator;

    public UniversityService() {
        this.dataStore = new DataStore(Path.of("data", "university-data.ser"));
        this.data = loadOrCreateData();
    }

    public boolean authenticateStudent(String id, String password) {
        for (Student student : data.students) {
            if (student.getId().equalsIgnoreCase(id) && student.getPassword().equals(password)) {
                currentStudent = student;
                currentProfessor = null;
                currentAdministrator = null;
                return true;
            }
        }
        return false;
    }

    public boolean authenticateProfessor(String id, String password) {
        for (Professor professor : data.professors) {
            if (professor.getId().equalsIgnoreCase(id) && professor.getPassword().equals(password)) {
                currentProfessor = professor;
                currentStudent = null;
                currentAdministrator = null;
                return true;
            }
        }
        return false;
    }

    public boolean authenticateAdministrator(String id, String password) {
        for (Administrator administrator : data.administrators) {
            if (administrator.getId().equalsIgnoreCase(id) && administrator.getPassword().equals(password)) {
                currentAdministrator = administrator;
                currentStudent = null;
                currentProfessor = null;
                return true;
            }
        }
        return false;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public Professor getCurrentProfessor() {
        return currentProfessor;
    }

    public Administrator getCurrentAdministrator() {
        return currentAdministrator;
    }

    public List<Student> getStudents() {
        return data.students;
    }

    public List<Professor> getProfessors() {
        return data.professors;
    }

    public List<Administrator> getAdministrators() {
        return data.administrators;
    }

    public List<Course> getCourses() {
        return data.courses;
    }

    public List<Enrollment> getEnrollments() {
        return data.enrollments;
    }

    public List<Grade> getGrades() {
        return data.grades;
    }

    public List<Course> getCurrentProfessorCourses() {
        List<Course> result = new ArrayList<>();
        if (currentProfessor == null) {
            return result;
        }

        for (Course course : data.courses) {
            if (currentProfessor.getId().equalsIgnoreCase(course.getProfessorId())) {
                result.add(course);
            }
        }
        result.sort(Comparator.comparing(Course::getId));
        return result;
    }

    public void addStudent(Student student) {
        data.students.add(student);
        save();
    }

    public boolean updateStudent(Student updatedStudent) {
        Student student = findStudentById(updatedStudent.getId());
        if (student == null) {
            return false;
        }

        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setBirthDate(updatedStudent.getBirthDate());
        student.setEmail(updatedStudent.getEmail());
        student.setDepartment(updatedStudent.getDepartment());
        student.setLevel(updatedStudent.getLevel());
        student.setAcademicYear(updatedStudent.getAcademicYear());
        student.setPassword(updatedStudent.getPassword());
        save();
        return true;
    }

    public boolean deleteStudent(String studentId) {
        boolean removed = data.students.removeIf(student -> student.getId().equalsIgnoreCase(studentId));
        if (removed) {
            data.enrollments.removeIf(enrollment -> enrollment.getStudentId().equalsIgnoreCase(studentId));
            data.grades.removeIf(grade -> grade.getStudentId().equalsIgnoreCase(studentId));
            save();
        }
        return removed;
    }

    public void addProfessor(Professor professor) {
        data.professors.add(professor);
        save();
    }

    public boolean updateProfessor(Professor updatedProfessor) {
        Professor professor = findProfessorById(updatedProfessor.getId());
        if (professor == null) {
            return false;
        }

        professor.setFullName(updatedProfessor.getFullName());
        professor.setSpecialty(updatedProfessor.getSpecialty());
        professor.setPassword(updatedProfessor.getPassword());
        save();
        return true;
    }

    public boolean deleteProfessor(String professorId) {
        boolean removed = data.professors.removeIf(professor -> professor.getId().equalsIgnoreCase(professorId));
        if (removed) {
            for (Course course : data.courses) {
                if (professorId.equalsIgnoreCase(course.getProfessorId())) {
                    course.setProfessorId("");
                }
            }
            save();
        }
        return removed;
    }

    public void addCourse(Course course) {
        data.courses.add(course);
        save();
    }

    public boolean updateCourse(Course updatedCourse) {
        Course course = findCourseById(updatedCourse.getId());
        if (course == null) {
            return false;
        }

        course.setName(updatedCourse.getName());
        course.setCoefficient(updatedCourse.getCoefficient());
        course.setHours(updatedCourse.getHours());
        course.setProfessorId(updatedCourse.getProfessorId());
        save();
        return true;
    }

    public boolean deleteCourse(String courseId) {
        boolean removed = data.courses.removeIf(course -> course.getId().equalsIgnoreCase(courseId));
        if (removed) {
            data.enrollments.removeIf(enrollment -> enrollment.getCourseId().equalsIgnoreCase(courseId));
            data.grades.removeIf(grade -> grade.getCourseId().equalsIgnoreCase(courseId));
            save();
        }
        return removed;
    }

    public boolean enrollStudent(String studentId, String courseId, String enrollmentDate) {
        for (Enrollment enrollment : data.enrollments) {
            if (enrollment.getStudentId().equalsIgnoreCase(studentId) && enrollment.getCourseId().equalsIgnoreCase(courseId)) {
                return false;
            }
        }

        data.enrollments.add(new Enrollment(studentId, courseId, enrollmentDate));
        save();
        return true;
    }

    public void saveGrade(String studentId, String courseId, String type, double value) {
        Grade grade = findOrCreateGrade(studentId, courseId);
        switch (type) {
            case "CC" -> grade.setContinuousAssessment(value);
            case "Examen" -> grade.setExam(value);
            case "Rattrapage" -> grade.setResit(value);
            default -> {
            }
        }
        save();
    }

    public List<Object[]> getStudentListRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : data.students) {
            rows.add(new Object[] {
                student.getId(),
                student.getLastName(),
                student.getFirstName(),
                student.getBirthDate(),
                student.getEmail(),
                student.getDepartment(),
                student.getLevel()
            });
        }
        return rows;
    }

    public List<Object[]> getEnrollmentRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : data.enrollments) {
            Student student = findStudentById(enrollment.getStudentId());
            Course course = findCourseById(enrollment.getCourseId());
            rows.add(new Object[] {
                enrollment.getStudentId(),
                student == null ? "" : student.getFullName(),
                enrollment.getCourseId(),
                course == null ? "" : course.getName(),
                enrollment.getEnrollmentDate()
            });
        }
        return rows;
    }

    public List<Object[]> getCourseRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Course course : data.courses) {
            Professor professor = findProfessorById(course.getProfessorId());
            rows.add(new Object[] {
                course.getId(),
                course.getName(),
                course.getCoefficient(),
                course.getHours(),
                professor == null ? "" : professor.getFullName()
            });
        }
        return rows;
    }

    public List<Object[]> getProfessorRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Professor professor : data.professors) {
            rows.add(new Object[] {
                professor.getId(),
                professor.getFullName(),
                professor.getSpecialty()
            });
        }
        return rows;
    }

    public List<Object[]> getAdministratorRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Administrator administrator : data.administrators) {
            rows.add(new Object[] {
                administrator.getId(),
                administrator.getFullName()
            });
        }
        return rows;
    }

    public List<Object[]> getAllAccountsRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : data.students) {
            rows.add(new Object[] {"Etudiant", student.getId(), student.getFullName(), student.getPassword()});
        }
        for (Professor professor : data.professors) {
            rows.add(new Object[] {"Enseignant", professor.getId(), professor.getFullName(), professor.getPassword()});
        }
        for (Administrator administrator : data.administrators) {
            rows.add(new Object[] {"Administrateur", administrator.getId(), administrator.getFullName(), administrator.getPassword()});
        }
        return rows;
    }

    public List<Object[]> getGradesBeforeResitRows(String studentId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : data.enrollments) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                continue;
            }

            Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
            Course course = findCourseById(enrollment.getCourseId());
            rows.add(new Object[] {
                enrollment.getCourseId(),
                course == null ? "" : course.getName(),
                displayNumber(grade.getContinuousAssessment()),
                displayNumber(grade.getExam()),
                String.format("%.2f", grade.calculateAverageBeforeResit()),
                grade.requiresResit() ? "Rattrapage" : "Valide"
            });
        }
        return rows;
    }

    public List<Object[]> getGradesAfterResitRows(String studentId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : data.enrollments) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                continue;
            }

            Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
            Course course = findCourseById(enrollment.getCourseId());
            rows.add(new Object[] {
                enrollment.getCourseId(),
                course == null ? "" : course.getName(),
                displayNumber(grade.getResit()),
                String.format("%.2f", grade.calculateAverageAfterResit()),
                grade.isValidatedAfterResit() ? "Valide" : "Non valide"
            });
        }
        return rows;
    }

    public double calculateAverageBeforeResit(String studentId) {
        double total = 0.0;
        int count = 0;
        for (Enrollment enrollment : data.enrollments) {
            if (enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                total += findOrCreateGrade(studentId, enrollment.getCourseId()).calculateAverageBeforeResit();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public double calculateAverageAfterResit(String studentId) {
        double total = 0.0;
        int count = 0;
        for (Enrollment enrollment : data.enrollments) {
            if (enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                total += findOrCreateGrade(studentId, enrollment.getCourseId()).calculateAverageAfterResit();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public List<Object[]> getStudentsInResitRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : data.students) {
            if (isStudentInResit(student.getId())) {
                rows.add(new Object[] {
                    student.getId(),
                    student.getFullName(),
                    String.format("%.2f", calculateAverageBeforeResit(student.getId())),
                    getBlockingModules(student.getId())
                });
            }
        }
        return rows;
    }

    public List<Object[]> getGraduatedStudentsRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : data.students) {
            if (isGraduated(student.getId())) {
                rows.add(new Object[] {
                    student.getId(),
                    student.getFullName(),
                    student.getLevel(),
                    String.format("%.2f", calculateAverageAfterResit(student.getId()))
                });
            }
        }
        return rows;
    }

    public List<Object[]> getFinalStudentStatusRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : data.students) {
            rows.add(new Object[] {
                student.getId(),
                student.getFullName(),
                String.format("%.2f", calculateAverageAfterResit(student.getId())),
                getFinalStatus(student.getId()),
                getBlockingModules(student.getId())
            });
        }
        return rows;
    }

    public List<Object[]> getCurrentProfessorModuleRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Course course : getCurrentProfessorCourses()) {
            rows.add(new Object[] {
                course.getId(),
                course.getName(),
                course.getCoefficient(),
                course.getHours()
            });
        }
        return rows;
    }

    public List<Object[]> getProfessorStudentRowsForCourse(String courseId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : data.enrollments) {
            if (!enrollment.getCourseId().equalsIgnoreCase(courseId)) {
                continue;
            }

            Student student = findStudentById(enrollment.getStudentId());
            Grade grade = findOrCreateGrade(enrollment.getStudentId(), courseId);
            rows.add(new Object[] {
                enrollment.getStudentId(),
                student == null ? "" : student.getFullName(),
                displayNumber(grade.getContinuousAssessment()),
                displayNumber(grade.getExam()),
                displayNumber(grade.getResit()),
                String.format("%.2f", grade.calculateAverageAfterResit())
            });
        }
        return rows;
    }

    public List<Object[]> getCurrentProfessorResitRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Course course : getCurrentProfessorCourses()) {
            for (Enrollment enrollment : data.enrollments) {
                if (!enrollment.getCourseId().equalsIgnoreCase(course.getId())) {
                    continue;
                }

                Grade grade = findOrCreateGrade(enrollment.getStudentId(), enrollment.getCourseId());
                if (!grade.requiresResit()) {
                    continue;
                }

                Student student = findStudentById(enrollment.getStudentId());
                rows.add(new Object[] {
                    enrollment.getStudentId(),
                    student == null ? "" : student.getFullName(),
                    course.getId(),
                    course.getName(),
                    String.format("%.2f", grade.calculateAverageBeforeResit())
                });
            }
        }
        return rows;
    }

    public List<Object[]> getCurrentStudentBeforeResitRows() {
        return currentStudent == null ? new ArrayList<>() : getGradesBeforeResitRows(currentStudent.getId());
    }

    public List<Object[]> getCurrentStudentAfterResitRows() {
        return currentStudent == null ? new ArrayList<>() : getGradesAfterResitRows(currentStudent.getId());
    }

    public double getCurrentStudentAverageBeforeResit() {
        return currentStudent == null ? 0.0 : calculateAverageBeforeResit(currentStudent.getId());
    }

    public double getCurrentStudentAverageAfterResit() {
        return currentStudent == null ? 0.0 : calculateAverageAfterResit(currentStudent.getId());
    }

    public String getCurrentStudentFinalStatus() {
        return currentStudent == null ? "Non admis" : getFinalStatus(currentStudent.getId());
    }

    public String getCurrentStudentBlockingModules() {
        return currentStudent == null ? "-" : getBlockingModules(currentStudent.getId());
    }

    public List<Object[]> getCurrentStudentEnrollmentRows() {
        List<Object[]> rows = new ArrayList<>();
        if (currentStudent == null) {
            return rows;
        }

        for (Enrollment enrollment : data.enrollments) {
            if (enrollment.getStudentId().equalsIgnoreCase(currentStudent.getId())) {
                Course course = findCourseById(enrollment.getCourseId());
                rows.add(new Object[] {
                    enrollment.getCourseId(),
                    course == null ? "" : course.getName(),
                    enrollment.getEnrollmentDate()
                });
            }
        }
        return rows;
    }

    public void save() {
        dataStore.save(data);
    }

    public boolean updateStudentPassword(String studentId, String newPassword) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return false;
        }
        student.setPassword(newPassword);
        save();
        return true;
    }

    public boolean updateProfessorPassword(String professorId, String newPassword) {
        Professor professor = findProfessorById(professorId);
        if (professor == null) {
            return false;
        }
        professor.setPassword(newPassword);
        save();
        return true;
    }

    public void addAdministrator(Administrator administrator) {
        data.administrators.add(administrator);
        save();
    }

    public boolean updateAdministrator(Administrator updatedAdministrator) {
        Administrator administrator = findAdministratorById(updatedAdministrator.getId());
        if (administrator == null) {
            return false;
        }
        administrator.setFullName(updatedAdministrator.getFullName());
        administrator.setPassword(updatedAdministrator.getPassword());
        save();
        return true;
    }

    public boolean deleteAdministrator(String administratorId) {
        boolean removed = data.administrators.removeIf(administrator -> administrator.getId().equalsIgnoreCase(administratorId));
        if (removed) {
            save();
        }
        return removed;
    }

    public boolean updateAdministratorPassword(String administratorId, String newPassword) {
        Administrator administrator = findAdministratorById(administratorId);
        if (administrator == null) {
            return false;
        }
        administrator.setPassword(newPassword);
        save();
        return true;
    }

    private String displayNumber(Double value) {
        return value == null ? "-" : String.format("%.2f", value);
    }

    private boolean isStudentInResit(String studentId) {
        for (Enrollment enrollment : data.enrollments) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                continue;
            }
            if (findOrCreateGrade(studentId, enrollment.getCourseId()).requiresResit()) {
                return true;
            }
        }
        return false;
    }

    private boolean isGraduated(String studentId) {
        boolean enrolled = false;
        for (Enrollment enrollment : data.enrollments) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                continue;
            }
            enrolled = true;
            if (!findOrCreateGrade(studentId, enrollment.getCourseId()).isValidatedAfterResit()) {
                return false;
            }
        }
        return enrolled;
    }

    public String getFinalStatus(String studentId) {
        return isGraduated(studentId) ? "Diplome" : "Non diplome";
    }

    public String getBlockingModules(String studentId) {
        List<String> blocking = new ArrayList<>();
        for (Enrollment enrollment : data.enrollments) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                continue;
            }

            Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
            if (!grade.isValidatedAfterResit()) {
                Course course = findCourseById(enrollment.getCourseId());
                blocking.add(course == null ? enrollment.getCourseId() : course.getName());
            }
        }

        return blocking.isEmpty() ? "-" : String.join(", ", blocking);
    }

    private Grade findOrCreateGrade(String studentId, String courseId) {
        for (Grade grade : data.grades) {
            if (grade.getStudentId().equalsIgnoreCase(studentId) && grade.getCourseId().equalsIgnoreCase(courseId)) {
                return grade;
            }
        }
        Grade grade = new Grade(studentId, courseId);
        data.grades.add(grade);
        return grade;
    }

    public Student findStudentById(String studentId) {
        for (Student student : data.students) {
            if (student.getId().equalsIgnoreCase(studentId)) {
                return student;
            }
        }
        return null;
    }

    public Professor findProfessorById(String professorId) {
        for (Professor professor : data.professors) {
            if (professor.getId().equalsIgnoreCase(professorId)) {
                return professor;
            }
        }
        return null;
    }

    public Administrator findAdministratorById(String administratorId) {
        for (Administrator administrator : data.administrators) {
            if (administrator.getId().equalsIgnoreCase(administratorId)) {
                return administrator;
            }
        }
        return null;
    }

    public Course findCourseById(String courseId) {
        for (Course course : data.courses) {
            if (course.getId().equalsIgnoreCase(courseId)) {
                return course;
            }
        }
        return null;
    }

    private UniversityData loadOrCreateData() {
        UniversityData loaded = (UniversityData) dataStore.load();
        if (loaded != null) {
            if (loaded.administrators == null) {
                loaded.administrators = new ArrayList<>();
            }
            if (loaded.administrators.stream().noneMatch(admin -> "ADMIN-01".equalsIgnoreCase(admin.getId()))) {
                loaded.administrators.add(new Administrator("ADMIN-01", "Admin Principal", "admin123"));
                dataStore.save(loaded);
            }
            return loaded;
        }

        UniversityData created = new UniversityData();

        created.students.add(new Student("2023-INFO-1452", "Amine", "Bensalem", "2004-05-14", "amine.bensalem@usthb.dz", "Informatique", "ING2", "2025/2026", "etudiant123"));
        created.students.add(new Student("2023-INFO-1789", "Sara", "Meziane", "2004-09-03", "sara.meziane@usthb.dz", "Informatique", "ING2", "2025/2026", "sara123"));

        created.professors.add(new Professor("PROF-INFO-01", "Dr Samir Khellaf", "Programmation Orientee Objet", "prof123"));
        created.professors.add(new Professor("PROF-INFO-02", "Dr Nadia Ait Ali", "Bases de donnees", "prof456"));
        created.administrators.add(new Administrator("ADMIN-01", "Admin Principal", "admin123"));

        created.courses.add(new Course("POO301", "Programmation Orientee Objet", 3, 45, "PROF-INFO-01"));
        created.courses.add(new Course("BDD302", "Bases de donnees", 2, 45, "PROF-INFO-02"));
        created.courses.add(new Course("SE303", "Systemes d'exploitation", 2, 45, "PROF-INFO-01"));
        created.courses.add(new Course("WEB304", "Developpement Web", 2, 30, "PROF-INFO-01"));

        created.enrollments.add(new Enrollment("2023-INFO-1452", "POO301", "2025-09-15"));
        created.enrollments.add(new Enrollment("2023-INFO-1452", "BDD302", "2025-09-15"));
        created.enrollments.add(new Enrollment("2023-INFO-1452", "SE303", "2025-09-15"));
        created.enrollments.add(new Enrollment("2023-INFO-1789", "POO301", "2025-09-15"));
        created.enrollments.add(new Enrollment("2023-INFO-1789", "WEB304", "2025-09-15"));
        created.enrollments.add(new Enrollment("2023-INFO-1789", "BDD302", "2025-09-15"));

        Grade g1 = new Grade("2023-INFO-1452", "POO301");
        g1.setContinuousAssessment(14.0);
        g1.setExam(11.0);
        created.grades.add(g1);

        Grade g2 = new Grade("2023-INFO-1452", "BDD302");
        g2.setContinuousAssessment(9.0);
        g2.setExam(8.0);
        g2.setResit(12.0);
        created.grades.add(g2);

        Grade g3 = new Grade("2023-INFO-1452", "SE303");
        g3.setContinuousAssessment(10.0);
        g3.setExam(7.0);
        created.grades.add(g3);

        Grade g4 = new Grade("2023-INFO-1789", "POO301");
        g4.setContinuousAssessment(16.0);
        g4.setExam(14.0);
        created.grades.add(g4);

        Grade g5 = new Grade("2023-INFO-1789", "WEB304");
        g5.setContinuousAssessment(15.0);
        g5.setExam(16.0);
        created.grades.add(g5);

        Grade g6 = new Grade("2023-INFO-1789", "BDD302");
        g6.setContinuousAssessment(7.0);
        g6.setExam(8.0);
        created.grades.add(g6);

        dataStore.save(created);
        return created;
    }

    private static class UniversityData extends DataStore.UniversityData implements Serializable {
        private static final long serialVersionUID = 1L;
        private final List<Student> students = new ArrayList<>();
        private final List<Professor> professors = new ArrayList<>();
        private List<Administrator> administrators = new ArrayList<>();
        private final List<Course> courses = new ArrayList<>();
        private final List<Enrollment> enrollments = new ArrayList<>();
        private final List<Grade> grades = new ArrayList<>();
    }
}
