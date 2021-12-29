import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;

enum Parity {
    everyWeek, //0
    odd, //1
    even //2
}

public class Lesson implements Serializable {
    private final String teacher;
    private final String subject;
    private final String classroom;
    private final String day;

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Parity parity;
    private final int interval;

    public Lesson(HashMap<String, String> icsFields) {
        this.teacher = icsFields.get("DESCRIPTION");
        this.subject = icsFields.get("SUMMARY");
        this.classroom = icsFields.get("LOCATION");
        this.day = LessonParser.parseLessonDay(icsFields.get("RRULE"));

        this.startTime = LessonParser.parseLessonStartTime(icsFields.get("DTSTART;TZID=Asia/Krasnoyarsk"));
        this.endTime = LessonParser.parseLessonEndTime(icsFields.get("DTEND;TZID=Asia/Krasnoyarsk"));
        this.parity = LessonParser.parseLessonParity(icsFields.get("DTSTART;TZID=Asia/Krasnoyarsk"));
        this.interval = LessonParser.parseLessonInterval(icsFields.get("RRULE"));
    }

    public String getTeacher() {
        return teacher;
    }

    public String getSubject() {
        return subject;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Parity getParity() {
        return parity;
    }

    public int getInterval() {
        return interval;
    }
}
