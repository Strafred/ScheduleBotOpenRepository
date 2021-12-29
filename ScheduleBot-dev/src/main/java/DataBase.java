import java.io.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DataBase {
    private static final int TIMER_TASK_PERIOD_MILLIS = 15000;

    private final HashMap<Long, User> database;

    public DataBase(String dataBaseBackup) throws IOException, ClassNotFoundException {
        String dataBaseBackupPath = System.getProperty("user.dir") + "\\" + dataBaseBackup;

        if (new File(dataBaseBackupPath).isFile()) {
            ObjectInputStream savedDataBaseIn = new ObjectInputStream(new FileInputStream(dataBaseBackup));
            database = (HashMap<Long, User>) savedDataBaseIn.readObject();
            System.out.println(database.size());
        } else {
            database = new HashMap<>();
        }

        Timer backupTimeTask = new Timer();
        backupTimeTask.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    File oldBackup = new File(dataBaseBackupPath);
                    oldBackup.delete();

                    ObjectOutputStream savedDataBaseOut = new ObjectOutputStream(new FileOutputStream(dataBaseBackup));
                    savedDataBaseOut.writeObject(database);
                    savedDataBaseOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, TIMER_TASK_PERIOD_MILLIS, TIMER_TASK_PERIOD_MILLIS);
    }

    public Schedule getUserSchedule(Long chatId) {
        User user = database.get(chatId);
        if (user == null) {
            return null;
        }

        return user.getSchedule();
    }

    public String getGroupNumber(Long chatId) {
        User user = database.get(chatId);
        if (user == null) {
            return null;
        }

        return user.getGroupNumber();
    }

    public void addUser(Long chatId, String groupNumber, Schedule schedule) {
        database.put(chatId, new User(groupNumber, schedule));
    }
}
