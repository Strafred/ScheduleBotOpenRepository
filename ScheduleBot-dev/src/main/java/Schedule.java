import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Schedule implements Serializable {
    private static final String DASH = "========================================================";

    Map<String, List<Lesson>> schedule;

    public Schedule(Map<String, List<Lesson>> schedule) {
        this.schedule = schedule;
    }

    public String getStringSchedule(String day) {
        StringBuffer stringBuffer = new StringBuffer("");

        schedule.get(day).forEach(lesson -> {
            stringBuffer.append(lesson.getTeacher()).append("\n");
            stringBuffer.append(lesson.getSubject()).append("\n");
            stringBuffer.append(lesson.getClassroom()).append("\n");
            stringBuffer.append(lesson.getStartTime().toString()).append("\n");
            stringBuffer.append(lesson.getEndTime().toString()).append("\n");

            stringBuffer.append(DASH).append("\n");
        });

        return stringBuffer.toString();
    }

    public List<Lesson> getListSchedule(String day) {
        return schedule.get(day);
    }

    public void setSchedule(Map<String, List<Lesson>> schedule) {
        this.schedule = schedule;
    }
}
