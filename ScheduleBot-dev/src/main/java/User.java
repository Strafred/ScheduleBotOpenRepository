import java.io.Serializable;

public class User implements Serializable {
    private final String groupNumber;
    private final Schedule schedule;

    public String getGroupNumber() {
        return groupNumber;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public User(String groupNumber, Schedule schedule) {
        this.groupNumber = groupNumber;
        this.schedule = schedule;
    }
}
