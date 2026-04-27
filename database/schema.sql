CREATE TABLE Professor (
    id VARCHAR(30) PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    specialty VARCHAR(120) NOT NULL,
    password VARCHAR(60) NOT NULL
);

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
);

CREATE TABLE Course (
    id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    coefficient INT NOT NULL CHECK (coefficient > 0),
    hours INT NOT NULL CHECK (hours > 0),
    professor_id VARCHAR(30) NULL,
    CONSTRAINT FK_Course_Professor FOREIGN KEY (professor_id) REFERENCES Professor(id)
);

CREATE TABLE Enrollment (
    student_id VARCHAR(30) NOT NULL,
    course_id VARCHAR(30) NOT NULL,
    enrollment_date DATE NOT NULL,
    CONSTRAINT PK_Enrollment PRIMARY KEY (student_id, course_id),
    CONSTRAINT FK_Enrollment_Student FOREIGN KEY (student_id) REFERENCES Student(id),
    CONSTRAINT FK_Enrollment_Course FOREIGN KEY (course_id) REFERENCES Course(id)
);

CREATE TABLE Grade (
    student_id VARCHAR(30) NOT NULL,
    course_id VARCHAR(30) NOT NULL,
    cc DECIMAL(5,2) NULL CHECK (cc BETWEEN 0 AND 20),
    exam DECIMAL(5,2) NULL CHECK (exam BETWEEN 0 AND 20),
    resit DECIMAL(5,2) NULL CHECK (resit BETWEEN 0 AND 20),
    CONSTRAINT PK_Grade PRIMARY KEY (student_id, course_id),
    CONSTRAINT FK_Grade_Student FOREIGN KEY (student_id) REFERENCES Student(id),
    CONSTRAINT FK_Grade_Course FOREIGN KEY (course_id) REFERENCES Course(id)
);
