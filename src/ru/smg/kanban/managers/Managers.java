package ru.smg.kanban.managers;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBackedTaskManager() {

        File file = new File("saveFile.csv");
        try {
            file.createNewFile();
            return new FileBackedTaskManager(getDefaultHistory(), file);
        } catch (IOException e) {
            return null;
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
