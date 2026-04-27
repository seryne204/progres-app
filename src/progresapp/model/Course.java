package progresapp.model;

import java.io.Serializable;

public class Course implements Serializable {
    private String id;
    private String name;
    private int coefficient;
    private int hours;
    private String professorId;

    public Course(String id, String name, int coefficient, int hours, String professorId) {
        this.id = id;
        this.name = name;
        this.coefficient = coefficient;
        this.hours = hours;
        this.professorId = professorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }
}
