package progresapp.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
    private final SqlServerManager sqlServerManager;
    private Student currentStudent;
    private Professor currentProfessor;
    private Administrator currentAdministrator;

    public UniversityService() {
        this.sqlServerManager = new SqlServerManager();
        try {
            sqlServerManager.initializeDatabase();
        } catch (SQLException exception) {
            throw new IllegalStateException("Connexion SQL Server impossible. Verifiez le driver JDBC et l'acces a la base DB.", exception);
        }
    }

    public boolean authenticateStudent(String id, String password) {
        String sql = "SELECT * FROM Student WHERE id = ? AND password = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentStudent = mapStudent(resultSet);
                    currentProfessor = null;
                    currentAdministrator = null;
                    return true;
                }
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    public boolean authenticateProfessor(String id, String password) {
        String sql = "SELECT * FROM Professor WHERE id = ? AND password = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentProfessor = mapProfessor(resultSet);
                    currentStudent = null;
                    currentAdministrator = null;
                    return true;
                }
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    public boolean authenticateAdministrator(String id, String password) {
        String sql = "SELECT * FROM Administrator WHERE id = ? AND password = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentAdministrator = new Administrator(resultSet.getString("id"), resultSet.getString("full_name"), resultSet.getString("password"));
                    currentStudent = null;
                    currentProfessor = null;
                    return true;
                }
            }
        } catch (SQLException ignored) {
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
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Student ORDER BY id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }
        } catch (SQLException ignored) {
        }
        return students;
    }

    public List<Professor> getProfessors() {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT * FROM Professor ORDER BY id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                professors.add(mapProfessor(resultSet));
            }
        } catch (SQLException ignored) {
        }
        return professors;
    }

    public List<Administrator> getAdministrators() {
        List<Administrator> administrators = new ArrayList<>();
        String sql = "SELECT * FROM Administrator ORDER BY id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                administrators.add(new Administrator(resultSet.getString("id"), resultSet.getString("full_name"), resultSet.getString("password")));
            }
        } catch (SQLException ignored) {
        }
        return administrators;
    }

    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course ORDER BY id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                courses.add(mapCourse(resultSet));
            }
        } catch (SQLException ignored) {
        }
        return courses;
    }

    public List<Enrollment> getEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Enrollment ORDER BY student_id, course_id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                enrollments.add(new Enrollment(
                    resultSet.getString("student_id"),
                    resultSet.getString("course_id"),
                    resultSet.getDate("enrollment_date").toString()
                ));
            }
        } catch (SQLException ignored) {
        }
        return enrollments;
    }

    public List<Grade> getGrades() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM Grade ORDER BY student_id, course_id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                grades.add(mapGrade(resultSet));
            }
        } catch (SQLException ignored) {
        }
        return grades;
    }

    public List<Course> getCurrentProfessorCourses() {
        List<Course> result = new ArrayList<>();
        if (currentProfessor == null) {
            return result;
        }
        String sql = "SELECT * FROM Course WHERE professor_id = ? ORDER BY id";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currentProfessor.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(mapCourse(resultSet));
                }
            }
        } catch (SQLException ignored) {
        }
        result.sort(Comparator.comparing(Course::getId));
        return result;
    }

    public void addStudent(Student student) {
        String sql = "INSERT INTO Student (id, first_name, last_name, birth_date, email, department, level_name, academic_year, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStudentStatement(statement, student);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE Student SET first_name = ?, last_name = ?, birth_date = ?, email = ?, department = ?, level_name = ?, academic_year = ?, password = ? WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setDate(3, Date.valueOf(student.getBirthDate()));
            statement.setString(4, student.getEmail());
            statement.setString(5, student.getDepartment());
            statement.setString(6, student.getLevel());
            statement.setString(7, student.getAcademicYear());
            statement.setString(8, student.getPassword());
            statement.setString(9, student.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        try (Connection connection = sqlServerManager.openConnection()) {
            deleteById(connection, "DELETE FROM Grade WHERE student_id = ?", studentId);
            deleteById(connection, "DELETE FROM Enrollment WHERE student_id = ?", studentId);
            return deleteById(connection, "DELETE FROM Student WHERE id = ?", studentId);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public void addProfessor(Professor professor) {
        String sql = "INSERT INTO Professor (id, full_name, specialty, password) VALUES (?, ?, ?, ?)";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, professor.getId());
            statement.setString(2, professor.getFullName());
            statement.setString(3, professor.getSpecialty());
            statement.setString(4, professor.getPassword());
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public boolean updateProfessor(Professor professor) {
        String sql = "UPDATE Professor SET full_name = ?, specialty = ?, password = ? WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, professor.getFullName());
            statement.setString(2, professor.getSpecialty());
            statement.setString(3, professor.getPassword());
            statement.setString(4, professor.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean deleteProfessor(String professorId) {
        try (Connection connection = sqlServerManager.openConnection()) {
            try (PreparedStatement clear = connection.prepareStatement("UPDATE Course SET professor_id = NULL WHERE professor_id = ?")) {
                clear.setString(1, professorId);
                clear.executeUpdate();
            }
            return deleteById(connection, "DELETE FROM Professor WHERE id = ?", professorId);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public void addCourse(Course course) {
        String sql = "INSERT INTO Course (id, name, coefficient, hours, professor_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillCourseStatement(statement, course);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE Course SET name = ?, coefficient = ?, hours = ?, professor_id = ? WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getName());
            statement.setInt(2, course.getCoefficient());
            statement.setInt(3, course.getHours());
            if (course.getProfessorId() == null || course.getProfessorId().isBlank()) {
                statement.setNull(4, Types.VARCHAR);
            } else {
                statement.setString(4, course.getProfessorId());
            }
            statement.setString(5, course.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean deleteCourse(String courseId) {
        try (Connection connection = sqlServerManager.openConnection()) {
            deleteById(connection, "DELETE FROM Grade WHERE course_id = ?", courseId);
            deleteById(connection, "DELETE FROM Enrollment WHERE course_id = ?", courseId);
            return deleteById(connection, "DELETE FROM Course WHERE id = ?", courseId);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean enrollStudent(String studentId, String courseId, String enrollmentDate) {
        String sql = "IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = ? AND course_id = ?) INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, courseId);
            statement.setString(3, studentId);
            statement.setString(4, courseId);
            statement.setDate(5, Date.valueOf(enrollmentDate));
            return statement.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public void saveGrade(String studentId, String courseId, String type, double value) {
        Grade current = findOrCreateGrade(studentId, courseId);
        switch (type) {
            case "CC" -> current.setContinuousAssessment(value);
            case "Examen" -> current.setExam(value);
            case "Rattrapage" -> current.setResit(value);
            default -> {
            }
        }

        String sql = """
            MERGE Grade AS target
            USING (SELECT ? AS student_id, ? AS course_id) AS source
            ON target.student_id = source.student_id AND target.course_id = source.course_id
            WHEN MATCHED THEN
                UPDATE SET cc = ?, exam = ?, resit = ?
            WHEN NOT MATCHED THEN
                INSERT (student_id, course_id, cc, exam, resit)
                VALUES (?, ?, ?, ?, ?);
            """;

        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, courseId);
            setNullableDouble(statement, 3, current.getContinuousAssessment());
            setNullableDouble(statement, 4, current.getExam());
            setNullableDouble(statement, 5, current.getResit());
            statement.setString(6, studentId);
            statement.setString(7, courseId);
            setNullableDouble(statement, 8, current.getContinuousAssessment());
            setNullableDouble(statement, 9, current.getExam());
            setNullableDouble(statement, 10, current.getResit());
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public List<Object[]> getStudentListRows() { return mapStudentsToRows(getStudents()); }
    private List<Object[]> mapStudentsToRows(List<Student> students) {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : students) {
            rows.add(new Object[] {student.getId(), student.getLastName(), student.getFirstName(), student.getBirthDate(), student.getEmail(), student.getDepartment(), student.getLevel()});
        }
        return rows;
    }

    public List<Object[]> getEnrollmentRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments()) {
            Student student = findStudentById(enrollment.getStudentId());
            Course course = findCourseById(enrollment.getCourseId());
            rows.add(new Object[] {enrollment.getStudentId(), student == null ? "" : student.getFullName(), enrollment.getCourseId(), course == null ? "" : course.getName(), enrollment.getEnrollmentDate()});
        }
        return rows;
    }

    public List<Object[]> getCourseRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Course course : getCourses()) {
            Professor professor = findProfessorById(course.getProfessorId());
            rows.add(new Object[] {course.getId(), course.getName(), course.getCoefficient(), course.getHours(), professor == null ? "" : professor.getFullName()});
        }
        return rows;
    }

    public List<Object[]> getProfessorRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Professor professor : getProfessors()) {
            rows.add(new Object[] {professor.getId(), professor.getFullName(), professor.getSpecialty()});
        }
        return rows;
    }

    public List<Object[]> getAdministratorRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Administrator administrator : getAdministrators()) {
            rows.add(new Object[] {administrator.getId(), administrator.getFullName()});
        }
        return rows;
    }

    public List<Object[]> getAllAccountsRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : getStudents()) rows.add(new Object[] {"Etudiant", student.getId(), student.getFullName(), student.getPassword()});
        for (Professor professor : getProfessors()) rows.add(new Object[] {"Enseignant", professor.getId(), professor.getFullName(), professor.getPassword()});
        for (Administrator administrator : getAdministrators()) rows.add(new Object[] {"Administrateur", administrator.getId(), administrator.getFullName(), administrator.getPassword()});
        return rows;
    }

    public List<Object[]> getGradesBeforeResitRows(String studentId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments()) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) continue;
            Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
            Course course = findCourseById(enrollment.getCourseId());
            rows.add(new Object[] {enrollment.getCourseId(), course == null ? "" : course.getName(), displayNumber(grade.getContinuousAssessment()), displayNumber(grade.getExam()), String.format("%.2f", grade.calculateAverageBeforeResit()), grade.requiresResit() ? "Rattrapage" : "Valide"});
        }
        return rows;
    }

    public List<Object[]> getGradesAfterResitRows(String studentId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments()) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) continue;
            Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
            Course course = findCourseById(enrollment.getCourseId());
            rows.add(new Object[] {enrollment.getCourseId(), course == null ? "" : course.getName(), displayNumber(grade.getResit()), String.format("%.2f", grade.calculateAverageAfterResit()), grade.isValidatedAfterResit() ? "Valide" : "Non valide"});
        }
        return rows;
    }

    public double calculateAverageBeforeResit(String studentId) { return calculateAverage(studentId, false); }
    public double calculateAverageAfterResit(String studentId) { return calculateAverage(studentId, true); }

    private double calculateAverage(String studentId, boolean afterResit) {
        double total = 0.0;
        int count = 0;
        for (Enrollment enrollment : getEnrollments()) {
            if (enrollment.getStudentId().equalsIgnoreCase(studentId)) {
                Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
                total += afterResit ? grade.calculateAverageAfterResit() : grade.calculateAverageBeforeResit();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public List<Object[]> getStudentsInResitRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : getStudents()) {
            if (isStudentInResit(student.getId())) {
                rows.add(new Object[] {student.getId(), student.getFullName(), String.format("%.2f", calculateAverageBeforeResit(student.getId())), getBlockingModules(student.getId())});
            }
        }
        return rows;
    }

    public List<Object[]> getGraduatedStudentsRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : getStudents()) {
            if (isGraduated(student.getId())) {
                rows.add(new Object[] {student.getId(), student.getFullName(), student.getLevel(), String.format("%.2f", calculateAverageAfterResit(student.getId()))});
            }
        }
        return rows;
    }

    public List<Object[]> getFinalStudentStatusRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Student student : getStudents()) {
            rows.add(new Object[] {student.getId(), student.getFullName(), String.format("%.2f", calculateAverageAfterResit(student.getId())), getFinalStatus(student.getId()), getBlockingModules(student.getId())});
        }
        return rows;
    }

    public List<Object[]> getCurrentProfessorModuleRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Course course : getCurrentProfessorCourses()) {
            rows.add(new Object[] {course.getId(), course.getName(), course.getCoefficient(), course.getHours()});
        }
        return rows;
    }

    public List<Object[]> getProfessorStudentRowsForCourse(String courseId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments()) {
            if (!enrollment.getCourseId().equalsIgnoreCase(courseId)) continue;
            Student student = findStudentById(enrollment.getStudentId());
            Grade grade = findOrCreateGrade(enrollment.getStudentId(), courseId);
            rows.add(new Object[] {enrollment.getStudentId(), student == null ? "" : student.getFullName(), displayNumber(grade.getContinuousAssessment()), displayNumber(grade.getExam()), displayNumber(grade.getResit()), String.format("%.2f", grade.calculateAverageAfterResit())});
        }
        return rows;
    }

    public List<Object[]> getCurrentProfessorResitRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Course course : getCurrentProfessorCourses()) {
            for (Enrollment enrollment : getEnrollments()) {
                if (!enrollment.getCourseId().equalsIgnoreCase(course.getId())) continue;
                Grade grade = findOrCreateGrade(enrollment.getStudentId(), enrollment.getCourseId());
                if (!grade.requiresResit()) continue;
                Student student = findStudentById(enrollment.getStudentId());
                rows.add(new Object[] {enrollment.getStudentId(), student == null ? "" : student.getFullName(), course.getId(), course.getName(), String.format("%.2f", grade.calculateAverageBeforeResit())});
            }
        }
        return rows;
    }

    public List<Object[]> getCurrentStudentBeforeResitRows() { return currentStudent == null ? new ArrayList<>() : getGradesBeforeResitRows(currentStudent.getId()); }
    public List<Object[]> getCurrentStudentAfterResitRows() { return currentStudent == null ? new ArrayList<>() : getGradesAfterResitRows(currentStudent.getId()); }
    public double getCurrentStudentAverageBeforeResit() { return currentStudent == null ? 0.0 : calculateAverageBeforeResit(currentStudent.getId()); }
    public double getCurrentStudentAverageAfterResit() { return currentStudent == null ? 0.0 : calculateAverageAfterResit(currentStudent.getId()); }
    public String getCurrentStudentFinalStatus() { return currentStudent == null ? "Non diplome" : getFinalStatus(currentStudent.getId()); }
    public String getCurrentStudentBlockingModules() { return currentStudent == null ? "-" : getBlockingModules(currentStudent.getId()); }

    public List<Object[]> getCurrentStudentEnrollmentRows() {
        List<Object[]> rows = new ArrayList<>();
        if (currentStudent == null) return rows;
        for (Enrollment enrollment : getEnrollments()) {
            if (enrollment.getStudentId().equalsIgnoreCase(currentStudent.getId())) {
                Course course = findCourseById(enrollment.getCourseId());
                rows.add(new Object[] {enrollment.getCourseId(), course == null ? "" : course.getName(), enrollment.getEnrollmentDate()});
            }
        }
        return rows;
    }

    public void save() {
    }

    public boolean updateStudentPassword(String studentId, String newPassword) {
        return updatePassword("UPDATE Student SET password = ? WHERE id = ?", newPassword, studentId);
    }

    public boolean updateProfessorPassword(String professorId, String newPassword) {
        return updatePassword("UPDATE Professor SET password = ? WHERE id = ?", newPassword, professorId);
    }

    public void addAdministrator(Administrator administrator) {
        String sql = "INSERT INTO Administrator (id, full_name, password) VALUES (?, ?, ?)";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, administrator.getId());
            statement.setString(2, administrator.getFullName());
            statement.setString(3, administrator.getPassword());
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public boolean updateAdministrator(Administrator administrator) {
        String sql = "UPDATE Administrator SET full_name = ?, password = ? WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, administrator.getFullName());
            statement.setString(2, administrator.getPassword());
            statement.setString(3, administrator.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean deleteAdministrator(String administratorId) {
        try (Connection connection = sqlServerManager.openConnection()) {
            return deleteById(connection, "DELETE FROM Administrator WHERE id = ?", administratorId);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean updateAdministratorPassword(String administratorId, String newPassword) {
        return updatePassword("UPDATE Administrator SET password = ? WHERE id = ?", newPassword, administratorId);
    }

    private boolean updatePassword(String sql, String newPassword, String id) {
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setString(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    private boolean isStudentInResit(String studentId) {
        for (Enrollment enrollment : getEnrollments()) {
            if (enrollment.getStudentId().equalsIgnoreCase(studentId) && findOrCreateGrade(studentId, enrollment.getCourseId()).requiresResit()) {
                return true;
            }
        }
        return false;
    }

    private boolean isGraduated(String studentId) {
        boolean enrolled = false;
        for (Enrollment enrollment : getEnrollments()) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) continue;
            enrolled = true;
            if (!findOrCreateGrade(studentId, enrollment.getCourseId()).isValidatedAfterResit()) return false;
        }
        return enrolled;
    }

    public String getFinalStatus(String studentId) {
        return isGraduated(studentId) ? "Diplome" : "Non diplome";
    }

    public String getBlockingModules(String studentId) {
        List<String> blocking = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments()) {
            if (!enrollment.getStudentId().equalsIgnoreCase(studentId)) continue;
            Grade grade = findOrCreateGrade(studentId, enrollment.getCourseId());
            if (!grade.isValidatedAfterResit()) {
                Course course = findCourseById(enrollment.getCourseId());
                blocking.add(course == null ? enrollment.getCourseId() : course.getName());
            }
        }
        return blocking.isEmpty() ? "-" : String.join(", ", blocking);
    }

    private Grade findOrCreateGrade(String studentId, String courseId) {
        String sql = "SELECT * FROM Grade WHERE student_id = ? AND course_id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapGrade(resultSet);
                }
            }
        } catch (SQLException ignored) {
        }
        return new Grade(studentId, courseId);
    }

    public Student findStudentById(String studentId) {
        String sql = "SELECT * FROM Student WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapStudent(resultSet);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public Professor findProfessorById(String professorId) {
        String sql = "SELECT * FROM Professor WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, professorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapProfessor(resultSet);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public Administrator findAdministratorById(String administratorId) {
        String sql = "SELECT * FROM Administrator WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, administratorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Administrator(resultSet.getString("id"), resultSet.getString("full_name"), resultSet.getString("password"));
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public Course findCourseById(String courseId) {
        String sql = "SELECT * FROM Course WHERE id = ?";
        try (Connection connection = sqlServerManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCourse(resultSet);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        return new Student(
            resultSet.getString("id"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getDate("birth_date").toString(),
            resultSet.getString("email"),
            resultSet.getString("department"),
            resultSet.getString("level_name"),
            resultSet.getString("academic_year"),
            resultSet.getString("password")
        );
    }

    private Professor mapProfessor(ResultSet resultSet) throws SQLException {
        return new Professor(
            resultSet.getString("id"),
            resultSet.getString("full_name"),
            resultSet.getString("specialty"),
            resultSet.getString("password")
        );
    }

    private Course mapCourse(ResultSet resultSet) throws SQLException {
        return new Course(
            resultSet.getString("id"),
            resultSet.getString("name"),
            resultSet.getInt("coefficient"),
            resultSet.getInt("hours"),
            resultSet.getString("professor_id") == null ? "" : resultSet.getString("professor_id")
        );
    }

    private Grade mapGrade(ResultSet resultSet) throws SQLException {
        Grade grade = new Grade(resultSet.getString("student_id"), resultSet.getString("course_id"));
        double cc = resultSet.getDouble("cc");
        if (!resultSet.wasNull()) grade.setContinuousAssessment(cc);
        double exam = resultSet.getDouble("exam");
        if (!resultSet.wasNull()) grade.setExam(exam);
        double resit = resultSet.getDouble("resit");
        if (!resultSet.wasNull()) grade.setResit(resit);
        return grade;
    }

    private void fillStudentStatement(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getId());
        statement.setString(2, student.getFirstName());
        statement.setString(3, student.getLastName());
        statement.setDate(4, Date.valueOf(student.getBirthDate()));
        statement.setString(5, student.getEmail());
        statement.setString(6, student.getDepartment());
        statement.setString(7, student.getLevel());
        statement.setString(8, student.getAcademicYear());
        statement.setString(9, student.getPassword());
    }

    private void fillCourseStatement(PreparedStatement statement, Course course) throws SQLException {
        statement.setString(1, course.getId());
        statement.setString(2, course.getName());
        statement.setInt(3, course.getCoefficient());
        statement.setInt(4, course.getHours());
        if (course.getProfessorId() == null || course.getProfessorId().isBlank()) {
            statement.setNull(5, Types.VARCHAR);
        } else {
            statement.setString(5, course.getProfessorId());
        }
    }

    private boolean deleteById(Connection connection, String sql, String id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private void setNullableDouble(PreparedStatement statement, int index, Double value) throws SQLException {
        if (value == null) statement.setNull(index, Types.DECIMAL);
        else statement.setDouble(index, value);
    }

    private String displayNumber(Double value) {
        return value == null ? "-" : String.format("%.2f", value);
    }
}
