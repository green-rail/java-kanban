package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String defaultFileName = "saveFile.csv";
    protected static final String firstLine = "id,type,name,status,description,duration,epic\n";
    private File saveFile;

    public FileBackedTaskManager(HistoryManager manager, String fileName) {
        super(manager);
        initStorage(fileName);
    }

    protected void initStorage(String fileName) {
        saveFile = new File(fileName);
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Can't create a file with this name.");
        }
    }

    private FileBackedTaskManager(HistoryManager manager, File saveFile) {
        super(manager);
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(String fileName) {
        var historyManager = new InMemoryHistoryManager();
        File saveFile = new File(fileName);
        if (saveFile.exists()) {
            var manager = new FileBackedTaskManager(historyManager, saveFile);
            manager.load();
            return manager;
        } else {
            throw new RuntimeException("Файл не найден.");
        }
    }

    public static void main(String[] args) {



        TaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), defaultFileName);
        var taskSound = new Task("Добавить звук",
                "Звуки ui; выигрыша; проигрыша; фоновая музыка", Status.NEW);
        taskManager.addTask(taskSound);

        var taskUI = new Task("Адаптивный ui",
                "Добавить поддержку портретного и горизонтального режимов экрана", Status.NEW);
        taskManager.addTask(taskUI);

        var epicSDK = new Epic("SDK Яндекс игр", "Интегрировать SDK Яндекс игр");
        int epicSDKId = taskManager.addTask(epicSDK);

        var subSDK1 = new Subtask("Изучить документацию",
                "За последнее время SDK обновился нужно быть в курсе изменений", Status.NEW, epicSDKId);
        taskManager.addTask(subSDK1);

        var subSDK2 = new Subtask("Лидерборд",
                "Оценить сложность реализации. Если сложно то целесообразность фичи под вопросом",
                Status.NEW,  epicSDKId);
        taskManager.addTask(subSDK2);

        var subSDK3 = new Subtask("Облачные сейвы",
                "Добавить сохранение прогресса", Status.NEW, epicSDKId);
        taskManager.addTask(subSDK3);

        var epicAsync = new Epic("Асинхронный код", "Всё что касается асинхронного кода");
        taskManager.addTask(epicAsync);

        taskManager.getTaskById(taskSound.getId());
        //taskManager.getTaskById(500);
        taskManager.getTaskById(taskSound.getId());
        printHistory(taskManager);

        taskManager.getSubtaskById(subSDK1.getId());
        taskManager.getSubtaskById(subSDK2.getId());
        printHistory(taskManager);

        taskManager.getTaskById(taskUI.getId());
        taskManager.getEpicById(epicSDK.getId());
        taskManager.getTaskById(taskUI.getId());
        taskManager.getEpicById(epicSDK.getId());
        printHistory(taskManager);

        System.out.println("Загружаем с диска...");
        var loadedManager = FileBackedTaskManager.loadFromFile(defaultFileName);
        printHistory(loadedManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        manager.getHistory().forEach(System.out::println);
    }

    protected static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        if (value == null || value.isBlank()) return result;
        String[] items = value.split(",");
        for (String item : items) {
            result.add(Integer.parseInt(item));
        }
        return result;
    }

    @Override
    public int addTask(Task task) {
        int index = super.addTask(task);
        save();
        return index;
    }

    @Override
    public void deleteTask(Task task) {
        super.deleteTask(task);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    protected void logHistory(Task task) {
        super.logHistory(task);
        save();
    }

    protected void load() {
        try (BufferedReader lineReader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            lineReader.readLine();
            line = lineReader.readLine();
            if (line == null || line.isEmpty()) {
                return;
            }
            while (!line.isBlank()) {
                var task = fromString(line);
                if (task != null) {
                    //super.addTask(task);
                    super.insertTask(task);
                }
                line = lineReader.readLine();
            }
            String historyLine = lineReader.readLine();
            if (historyLine == null || historyLine.isBlank()) {
                return;
            }
            var history = historyFromString(historyLine);
            if (history.isEmpty()) {
                return;
            }
            for (int id : history) {
                var task = getTask(id);
                if (task != null) {
                    historyManager.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException();
        }
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write(firstLine);
            for (Task task : getAllTasks()) {
                writer.write(task.serialize() + "\n");
            }
            for (Task epic : getAllEpics()) {
                writer.write(epic.serialize() + "\n");
            }
            for (Task subtask : getAllSubtasks()) {
                writer.write(subtask.serialize() + "\n");
            }
            writer.write("\n");
            for (Task task : getHistory()) {
                writer.write(task.getId() + ",");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException();
        }
    }

    protected Task fromString(String value) {

        String[] split = value.split(",");

        if (split.length < 5) {
            return null;
        }

        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status;
        try {
            status = Status.valueOf(split[3]);
        } catch (IllegalArgumentException e) {
            System.out.println(split[3]);
            return null;
        }

        Duration duration = Duration.ofMinutes(Integer.parseInt(split[5]));
        LocalDateTime startTime = LocalDateTime.parse(split[6], Task.startTimeFormatter);
        String description = split[4];
        switch (split[1]) {
            case "TASK" : {
                var task = new Task(id, name, description, status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            }
            case "EPIC" : {
                return new Epic(id, name, description);
            }
            case "SUBTASK" : {
                int epicId = Integer.parseInt(split[split.length - 1]);
                var subtask = new Subtask(id, name, description, status, epicId);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                return subtask;
            }
            default: return null;
        }
    }
}
