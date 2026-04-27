package progresapp.model;

public class ProfessorScheduleEntry {
    private final String day;
    private final String time;
    private final String module;
    private final String section;
    private final String room;

    public ProfessorScheduleEntry(String day, String time, String module, String section, String room) {
        this.day = day;
        this.time = time;
        this.module = module;
        this.section = section;
        this.room = room;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getModule() {
        return module;
    }

    public String getSection() {
        return section;
    }

    public String getRoom() {
        return room;
    }
}
