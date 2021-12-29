import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ScheduleParser {
    static final String TABLE_NSU_URL = "https://table.nsu.ru/ics/group/";
    static final String LESSON_BLOCK_START = "BEGIN:VEVENT";
    static final String LESSON_BLOCK_END = "END:VEVENT";
    static final String DIVIDER = ":";

    public static Schedule parseGroupSchedule(String groupNumber) {
        String groupScheduleURL = TABLE_NSU_URL + groupNumber;

        try {
            BufferedReader icsReader;
            icsReader = new BufferedReader(new InputStreamReader(new URL(groupScheduleURL).openStream(), StandardCharsets.UTF_8));
            List<Lesson> lessons = new ArrayList<>();

            String icsLine;
            while ((icsLine = icsReader.readLine()) != null) {
                if (icsLine.equals(LESSON_BLOCK_START)) {
                    lessons.add(parseLessonBlock(icsReader));
                }
            }

            Multimap<String, Lesson> lessonByDayMultimap = ArrayListMultimap.create();
            List<String> daysList = new ArrayList<>();

            lessons.forEach(lesson -> {
                var dayOfLesson = lesson.getDay();
                lessonByDayMultimap.put(dayOfLesson, lesson);

                if (!daysList.contains(dayOfLesson)) {
                    daysList.add(dayOfLesson);
                }
            });

            Map<String, List<Lesson>> groupSchedule = new HashMap<>();

            for (String day : daysList) {
                List<Lesson> dayLessons = new ArrayList<>(lessonByDayMultimap.get(day));
                groupSchedule.put(day, dayLessons);
            }
            return new Schedule(groupSchedule);

        } catch (IOException e) {
            return null;
        }
    }

    private static Lesson parseLessonBlock(BufferedReader icsReader) throws IOException {
        HashMap<String, String> icsFields = new HashMap<>();

        String icsLine;
        while (!Objects.equals(icsLine = icsReader.readLine(), LESSON_BLOCK_END)) {
            int dividerIndex = icsLine.indexOf(DIVIDER);

            String fieldName = icsLine.substring(0, dividerIndex);
            String fieldValue = icsLine.substring(dividerIndex + 1);
            icsFields.put(fieldName, fieldValue);
        }

        return new Lesson(icsFields);
    }
}
