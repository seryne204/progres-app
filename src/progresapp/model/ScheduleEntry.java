package progresapp.model;

public class ScheduleEntry {
    private final String day;
    private final String time;
    private final String module;
    private final String room;

    public ScheduleEntry(String day, String time, String module, String room) {
        this.day = day;
        this.time = time;
        this.module = module;
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

    public String getRoom() {
        return room;
    }
}
