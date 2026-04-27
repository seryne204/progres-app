package progresapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlServerManager {
    private static final String URL =
        "jdbc:sqlserver://localhost;"
            + "instanceName=TEW_SQLEXPRESS;"
            + "databaseName=DB;"
            + "integratedSecurity=true;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void initializeDatabase() throws SQLException {
        try (Connection connection = openConnection(); Statement statement = connection.createStatement()) {
            statement.execute("""
                IF OBJECT_ID('Administrator', 'U') IS NULL
                CREATE TABLE Administrator (
                    id VARCHAR(30) PRIMARY KEY,
                    full_name VARCHAR(120) NOT NULL,
                    password VARCHAR(60) NOT NULL
                )
                """);

            statement.execute("""
                IF OBJECT_ID('Professor', 'U') IS NULL
                CREATE TABLE Professor (
                    id VARCHAR(30) PRIMARY KEY,
                    full_name VARCHAR(120) NOT NULL,
                    specialty VARCHAR(120) NOT NULL,
                    password VARCHAR(60) NOT NULL
                )
                """);

            statement.execute("""
                IF OBJECT_ID('Student', 'U') IS NULL
                CREATE TABLE Student (
                    id VARCHAR(30) PRIMARY KEY,
                    first_name VARCHAR(80) NOT NULL,
                    last_name VARCHAR(80) NOT NULL,
                    birth_date DATE NOT NULL,
                    email VARCHAR(120) NOT NULL UNIQUE,
                    department VARCHAR(80) NOT NULL,
                    level_name VARCHAR(40) NOT NULL,
                    academic_year VARCHAR(20) NOT NULL,
                    password VARCHAR(60) NOT NULL
                )
                """);

            statement.execute("""
                IF OBJECT_ID('Course', 'U') IS NULL
                CREATE TABLE Course (
                    id VARCHAR(30) PRIMARY KEY,
                    name VARCHAR(120) NOT NULL,
                    coefficient INT NOT NULL CHECK (coefficient > 0),
                    hours INT NOT NULL CHECK (hours > 0),
                    professor_id VARCHAR(30) NULL,
                    CONSTRAINT FK_Course_Professor FOREIGN KEY (professor_id) REFERENCES Professor(id)
                )
                """);

            statement.execute("""
                IF OBJECT_ID('Enrollment', 'U') IS NULL
                CREATE TABLE Enrollment (
                    student_id VARCHAR(30) NOT NULL,
                    course_id VARCHAR(30) NOT NULL,
                    enrollment_date DATE NOT NULL,
                    CONSTRAINT PK_Enrollment PRIMARY KEY (student_id, course_id),
                    CONSTRAINT FK_Enrollment_Student FOREIGN KEY (student_id) REFERENCES Student(id),
                    CONSTRAINT FK_Enrollment_Course FOREIGN KEY (course_id) REFERENCES Course(id)
                )
                """);

            statement.execute("""
                IF OBJECT_ID('Grade', 'U') IS NULL
                CREATE TABLE Grade (
                    student_id VARCHAR(30) NOT NULL,
                    course_id VARCHAR(30) NOT NULL,
                    cc DECIMAL(5,2) NULL CHECK (cc BETWEEN 0 AND 20),
                    exam DECIMAL(5,2) NULL CHECK (exam BETWEEN 0 AND 20),
                    resit DECIMAL(5,2) NULL CHECK (resit BETWEEN 0 AND 20),
                    CONSTRAINT PK_Grade PRIMARY KEY (student_id, course_id),
                    CONSTRAINT FK_Grade_Student FOREIGN KEY (student_id) REFERENCES Student(id),
                    CONSTRAINT FK_Grade_Course FOREIGN KEY (course_id) REFERENCES Course(id)
                )
                """);

            seedDemoData(statement);
        }
    }

    private void seedDemoData(Statement statement) throws SQLException {
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Administrator WHERE id = 'ADMIN-01')
            INSERT INTO Administrator (id, full_name, password)
            VALUES ('ADMIN-01', 'Admin Principal', 'admin123')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Professor WHERE id = 'PROF-INFO-01')
            INSERT INTO Professor (id, full_name, specialty, password)
            VALUES ('PROF-INFO-01', 'Dr Samir Khellaf', 'Programmation Orientee Objet', 'prof123')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Professor WHERE id = 'PROF-INFO-02')
            INSERT INTO Professor (id, full_name, specialty, password)
            VALUES ('PROF-INFO-02', 'Dr Nadia Ait Ali', 'Bases de donnees', 'prof456')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Student WHERE id = '2023-INFO-1452')
            INSERT INTO Student (id, first_name, last_name, birth_date, email, department, level_name, academic_year, password)
            VALUES ('2023-INFO-1452', 'Amine', 'Bensalem', '2004-05-14', 'amine.bensalem@usthb.dz', 'Informatique', 'ING2', '2025/2026', 'etudiant123')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Student WHERE id = '2023-INFO-1789')
            INSERT INTO Student (id, first_name, last_name, birth_date, email, department, level_name, academic_year, password)
            VALUES ('2023-INFO-1789', 'Sara', 'Meziane', '2004-09-03', 'sara.meziane@usthb.dz', 'Informatique', 'ING2', '2025/2026', 'sara123')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Course WHERE id = 'POO301')
            INSERT INTO Course (id, name, coefficient, hours, professor_id)
            VALUES ('POO301', 'Programmation Orientee Objet', 3, 45, 'PROF-INFO-01')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Course WHERE id = 'BDD302')
            INSERT INTO Course (id, name, coefficient, hours, professor_id)
            VALUES ('BDD302', 'Bases de donnees', 2, 45, 'PROF-INFO-02')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Course WHERE id = 'SE303')
            INSERT INTO Course (id, name, coefficient, hours, professor_id)
            VALUES ('SE303', 'Systemes d''exploitation', 2, 45, 'PROF-INFO-01')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Course WHERE id = 'WEB304')
            INSERT INTO Course (id, name, coefficient, hours, professor_id)
            VALUES ('WEB304', 'Developpement Web', 2, 30, 'PROF-INFO-01')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = '2023-INFO-1452' AND course_id = 'POO301')
            INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES ('2023-INFO-1452', 'POO301', '2025-09-15')
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = '2023-INFO-1452' AND course_id = 'BDD302')
            INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES ('2023-INFO-1452', 'BDD302', '2025-09-15')
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = '2023-INFO-1452' AND course_id = 'SE303')
            INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES ('2023-INFO-1452', 'SE303', '2025-09-15')
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = '2023-INFO-1789' AND course_id = 'POO301')
            INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES ('2023-INFO-1789', 'POO301', '2025-09-15')
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = '2023-INFO-1789' AND course_id = 'WEB304')
            INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES ('2023-INFO-1789', 'WEB304', '2025-09-15')
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Enrollment WHERE student_id = '2023-INFO-1789' AND course_id = 'BDD302')
            INSERT INTO Enrollment (student_id, course_id, enrollment_date) VALUES ('2023-INFO-1789', 'BDD302', '2025-09-15')
            """);

        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Grade WHERE student_id = '2023-INFO-1452' AND course_id = 'POO301')
            INSERT INTO Grade (student_id, course_id, cc, exam, resit) VALUES ('2023-INFO-1452', 'POO301', 14.0, 11.0, NULL)
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Grade WHERE student_id = '2023-INFO-1452' AND course_id = 'BDD302')
            INSERT INTO Grade (student_id, course_id, cc, exam, resit) VALUES ('2023-INFO-1452', 'BDD302', 9.0, 8.0, 12.0)
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Grade WHERE student_id = '2023-INFO-1452' AND course_id = 'SE303')
            INSERT INTO Grade (student_id, course_id, cc, exam, resit) VALUES ('2023-INFO-1452', 'SE303', 10.0, 7.0, NULL)
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Grade WHERE student_id = '2023-INFO-1789' AND course_id = 'POO301')
            INSERT INTO Grade (student_id, course_id, cc, exam, resit) VALUES ('2023-INFO-1789', 'POO301', 16.0, 14.0, NULL)
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Grade WHERE student_id = '2023-INFO-1789' AND course_id = 'WEB304')
            INSERT INTO Grade (student_id, course_id, cc, exam, resit) VALUES ('2023-INFO-1789', 'WEB304', 15.0, 16.0, NULL)
            """);
        statement.execute("""
            IF NOT EXISTS (SELECT 1 FROM Grade WHERE student_id = '2023-INFO-1789' AND course_id = 'BDD302')
            INSERT INTO Grade (student_id, course_id, cc, exam, resit) VALUES ('2023-INFO-1789', 'BDD302', 7.0, 8.0, NULL)
            """);
    }
}
