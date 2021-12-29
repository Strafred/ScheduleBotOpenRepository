import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ScheduleBot extends TelegramLongPollingBot {

    private static DataBase dataBase;
    private static Collection<KeyboardRow> mainMenu;
    private static Collection<KeyboardRow> editingMenu;

    private static final String GROUP_NUMBER_FORMAT = "[1-2][0-9]{4}(\\.[1-4])?";
    private static final String TOKEN = "5076105079:AAEpt9sWk4vuWG5-HUy7LAGbZnSAzYt1Kb0";
    private static final String USERNAME = "@nsuScheduleTestBot";

    public ScheduleBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendTextResponse(Message message, String text) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(text)
                        .build()
        );
    }

    private void sendKeyboardResponse(Message message, String text, Collection<KeyboardRow> keyboard) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(text)
                        .replyMarkup(ReplyKeyboardMarkup.builder().keyboard(keyboard).resizeKeyboard(true).build())
                        .build()
        );
    }

    private boolean isGroupNumberValid(String groupNumber) {
        return Pattern.compile(GROUP_NUMBER_FORMAT).matcher(groupNumber).matches();
    }

    private boolean isWeekDay(String userMessage) {
        String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return Arrays.stream(weekDays).anyMatch(userMessage::contains);
    }

    private String getCodeByDay(String day) {
        return day.substring(0, 2).toUpperCase();
    }

    private String getDayFromEdit(String editString) {
        return editString.substring(editString.indexOf("Edit") + "Edit".length() + 1, editString.lastIndexOf(" "));
    }

    private boolean isEditingCallback(String userMessage) {
        return userMessage.contains("[") && userMessage.contains("]");
    }

    private Integer getLessonIdFromEditingCallback(String userMessage) {
        String lessonId = userMessage.substring(userMessage.lastIndexOf("[") + 1, userMessage.lastIndexOf(" "));
        return Integer.parseInt(lessonId);
    }

    private String getDayCodeFromEditingCallback(String userMessage) {
        return userMessage.substring(userMessage.lastIndexOf(" ") + 1, userMessage.lastIndexOf("]"));
    }

    public void handleMessage(Message message) throws TelegramApiException {
        if (message.hasText()) {
            String userMessage = message.getText();
            Long chatId = message.getChatId();

            if (userMessage.contains("/start")) {
                sendKeyboardResponse(message,
                        """
                                Hello i'm nsu schedule test bot!
                                Please write your group number!
                                                                        
                                If you want to completely change your group - write your group number again!
                                """
                        , mainMenu);
            } else if (userMessage.contains("Main Menu")) {
                sendKeyboardResponse(message, "Choose day", mainMenu);
            } else if (userMessage.equals("Edit")) {
                String groupNumber = dataBase.getGroupNumber(chatId);
                if (groupNumber != null) {
                    sendKeyboardResponse(message, "Choose day, which you want to correct", editingMenu);
                } else {
                    sendTextResponse(message, "You didn't choose your group.\nPlease do it!");
                }
            } else if (userMessage.contains("Edit")) {
                String groupNumber = dataBase.getGroupNumber(chatId);
                if (groupNumber != null) {
                    String dayCode = getCodeByDay(getDayFromEdit(userMessage));
                    List<Lesson> lessons = dataBase.getUserSchedule(chatId).getListSchedule(dayCode);

                    var lessonsToDeleteList = Initializations.initLessonDeletingMenu(dayCode, lessons);

                    sendKeyboardResponse(message, "Choose lesson", lessonsToDeleteList);
                } else {
                    sendKeyboardResponse(message, "You didn't choose your group.\nPlease do it!", mainMenu);
                }
            } else if (isWeekDay(userMessage)) {
                String groupNumber = dataBase.getGroupNumber(chatId);
                if (groupNumber == null) {
                    sendTextResponse(message, "You didn't choose your group.\nPlease do it!");
                } else {
                    String dayCode = getCodeByDay(userMessage);
                    String schedule = dataBase.getUserSchedule(chatId).getStringSchedule(dayCode);
                    sendTextResponse(message, Objects.requireNonNullElse(schedule, "You haven't lessons today.\nLucky!!"));
                }
            } else if (isEditingCallback(userMessage)) {
                String groupNumber = dataBase.getGroupNumber(chatId);
                if (groupNumber != null) {
                    int lessonIndex = getLessonIdFromEditingCallback(userMessage);
                    String dayCode = getDayCodeFromEditingCallback(userMessage);
                    Lesson removedLesson = dataBase.getUserSchedule(chatId).getListSchedule(dayCode).remove(lessonIndex);

                    if (removedLesson != null) {
                        List<Lesson> lessons = dataBase.getUserSchedule(chatId).getListSchedule(dayCode);
                        var lessonsToDeleteList = Initializations.initLessonDeletingMenu(dayCode, lessons);
                        sendKeyboardResponse(message, "Lesson deleted successfully!", lessonsToDeleteList);
                    } else {
                        sendKeyboardResponse(message, "You haven't lessons today.\nLucky!!", mainMenu);
                    }
                } else {
                    sendKeyboardResponse(message, "You didn't choose your group.\nPlease do it!", mainMenu);
                }
            } else {
                if (isGroupNumberValid(userMessage)) {
                    Schedule schedule = ScheduleParser.parseGroupSchedule(userMessage);
                    if (schedule != null) {
                        dataBase.addUser(chatId, userMessage, schedule);
                        sendKeyboardResponse(message, "Group number correct!\nNow you can choose the day!", mainMenu);
                    } else {
                        sendKeyboardResponse(message, "Invalid group number.\nPlease try again!", mainMenu);
                    }
                } else {
                    sendKeyboardResponse(message, "Invalid group number.\nPlease try again!", mainMenu);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        dataBase = new DataBase("DATABASE.txt");
        mainMenu = Initializations.initMainMenu();
        editingMenu = Initializations.initEditingMenu();

        ScheduleBot bot = new ScheduleBot(new DefaultBotOptions());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        telegramBotsApi.registerBot(bot);
    }
}
