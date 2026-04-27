package progresapp.model;

public class Absence {
    private final String module;
    private final String date;
    private final String reason;
    private final boolean justified;

    public Absence(String module, String date, String reason, boolean justified) {
        this.module = module;
        this.date = date;
        this.reason = reason;
        this.justified = justified;
    }

    public String getModule() {
        return module;
    }

    public String getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public boolean isJustified() {
        return justified;
    }
}
