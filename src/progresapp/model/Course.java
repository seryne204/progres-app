package progresapp.model;

public class Course {
    private final String code;
    private final String title;
    private final int coefficient;
    private final int credits;

    public Course(String code, String title, int coefficient, int credits) {
        this.code = code;
        this.title = title;
        this.coefficient = coefficient;
        this.credits = credits;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public int getCredits() {
        return credits;
    }
}
