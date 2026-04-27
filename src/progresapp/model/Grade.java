package progresapp.model;

public class Grade {
    private final Course course;
    private final double mark;

    public Grade(Course course, double mark) {
        this.course = course;
        this.mark = mark;
    }

    public Course getCourse() {
        return course;
    }

    public double getMark() {
        return mark;
    }

    public String getStatus() {
        return mark >= 10.0 ? "Valide" : "Rattrapage";
    }
}
