# Conception du projet

## UML

```mermaid
classDiagram
    class Student {
      +id : String
      +firstName : String
      +lastName : String
      +birthDate : String
      +email : String
      +department : String
      +level : String
      +academicYear : String
      +password : String
    }

    class Professor {
      +id : String
      +fullName : String
      +specialty : String
      +password : String
    }

    class Course {
      +id : String
      +name : String
      +coefficient : int
      +hours : int
      +professorId : String
    }

    class Enrollment {
      +studentId : String
      +courseId : String
      +enrollmentDate : String
    }

    class Grade {
      +studentId : String
      +courseId : String
      +continuousAssessment : Double
      +exam : Double
      +resit : Double
      +calculateAverageBeforeResit() double
      +calculateAverageAfterResit() double
    }

    Professor "1" --> "*" Course : teaches
    Student "1" --> "*" Enrollment : has
    Course "1" --> "*" Enrollment : contains
    Student "1" --> "*" Grade : receives
    Course "1" --> "*" Grade : evaluated in
```

## MLD

- `Student(id PK, first_name, last_name, birth_date, email, department, level, academic_year, password)`
- `Professor(id PK, full_name, specialty, password)`
- `Course(id PK, name, coefficient, hours, professor_id FK -> Professor.id)`
- `Enrollment(student_id FK -> Student.id, course_id FK -> Course.id, enrollment_date, PK(student_id, course_id))`
- `Grade(student_id FK -> Student.id, course_id FK -> Course.id, cc, exam, resit, PK(student_id, course_id))`
