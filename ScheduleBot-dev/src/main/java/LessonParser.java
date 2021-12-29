import java.time.LocalTime;

public class LessonParser {
    static final int DAY_FORMAT_SIZE = 2;
    static final int CLOCK_FORMAT_SIZE = 2;

    public static String parseLessonDay(String rRule) {
        int dayStartIndex = rRule.indexOf("BYDAY=") + "BYDAY=".length();
        int dayEndIndex = dayStartIndex + DAY_FORMAT_SIZE;
        return rRule.substring(dayStartIndex, dayEndIndex);
    }

    public static LocalTime parseLessonStartTime(String dtStart) {
        int startTimeIndex = dtStart.indexOf("T") + 1;
        int hours = Integer.parseInt(dtStart.substring(startTimeIndex, startTimeIndex + CLOCK_FORMAT_SIZE));
        int minutes = Integer.parseInt(dtStart.substring(startTimeIndex + CLOCK_FORMAT_SIZE,
                startTimeIndex + 2 * CLOCK_FORMAT_SIZE));

        return LocalTime.of(hours, minutes);
    }

    public static LocalTime parseLessonEndTime(String dtEnd) {
        int endTimeIndex = dtEnd.indexOf("T") + 1;
        int hours = Integer.parseInt(dtEnd.substring(endTimeIndex, endTimeIndex + CLOCK_FORMAT_SIZE));
        int minutes = Integer.parseInt(dtEnd.substring(endTimeIndex + CLOCK_FORMAT_SIZE, endTimeIndex + 2 * CLOCK_FORMAT_SIZE));

        return LocalTime.of(hours, minutes);
    }

    public static Parity parseLessonParity(String dtStart) {
        return Parity.everyWeek;
    }

    public static int parseLessonInterval(String rrule) {
        return 1;
    }
}
