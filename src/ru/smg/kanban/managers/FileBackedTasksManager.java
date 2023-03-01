package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String firstLine = "id,type,name,status,description,epic\n";
    private final File saveFile;

    public FileBackedTasksManager(HistoryManager manager, File saveFile) {
        super(manager);
        this.saveFile = saveFile;
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        var historyManager = new InMemoryHistoryManager();
        var manager = new FileBackedTasksManager(historyManager, file);
        manager.load();
        return manager;
    }

    public static void main(String[] args) {

        File file = new File("saveFile.csv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        TaskManager taskManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        var taskSound = new Task(taskManager.getNextId(), "Добавить звук",
                "Звуки ui; выигрыша; проигрыша; фоновая музыка", Status.NEW);
        taskManager.addTask(taskSound);

        var taskUI = new Task(taskManager.getNextId(), "Адаптивный ui",
                "Добавить поддержку портретного и горизонтального режимов экрана", Status.NEW);
        taskManager.addTask(taskUI);

        var epicSDK = new Epic(taskManager.getNextId(),
                "SDK Яндекс игр", "Интегрировать SDK Яндекс игр", new ArrayList<>());
        taskManager.addTask(epicSDK);

        var subSDK1 = new Subtask(taskManager.getNextId(), "Изучить документацию",
                "За последнее время SDK обновился нужно быть в курсе изменений", Status.NEW, epicSDK);
        taskManager.addTask(subSDK1);

        var subSDK2 = new Subtask(taskManager.getNextId(), "Лидерборд",
                "Оценить сложность реализации. Если сложно то целесообразность фичи под вопросом",
                Status.NEW,  epicSDK);
        taskManager.addTask(subSDK2);

        var subSDK3 = new Subtask(taskManager.getNextId(), "Облачные сейвы",
                "Добавить сохранение прогресса", Status.NEW, epicSDK);
        taskManager.addTask(subSDK3);

        var epicAsync = new Epic( taskManager.getNextId(), "Асинхронный код",
                "Всё что касается асинхронного кода", new ArrayList<>());
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
        var loadedManager = FileBackedTasksManager.loadFromFile(file);
        printHistory(loadedManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        manager.getHistory().forEach(System.out::println);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        String[] items = value.split(",");
        for (String item : items) {
            result.add(Integer.parseInt(item));
        }
        return result;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
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

    private void load() {
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
                    loadTask(task);
                }
                line = lineReader.readLine();
            }
            var history = historyFromString(lineReader.readLine());
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

    private void loadTask(Task task) {
        super.addTask(task);
        if (nextId <= task.getId()) {
            nextId = task.getId() + 1;
        }
    }

    private void save() {
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

    private Task fromString(String value) {

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

        StringBuilder description = new StringBuilder();
        boolean isTask = "TASK".equals(split[1]);
        if (isTask || "EPIC".equals(split[1])) {
            for (int i = 4; i < split.length; i++) {
                description.append(split[i]);
            }
            return isTask ? new Task(id, name, description.toString(), status) :
                    new Epic(id, name, description.toString(), new ArrayList<>());
        }
        for (int i = 4; i < split.length - 1; i++) {
            description.append(split[i]);
        }
        int epicId = Integer.parseInt(split[split.length - 1]);
        return new Subtask(id, name, description.toString(), status, (Epic) getTask(epicId));
    }
}
