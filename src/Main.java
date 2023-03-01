import ru.smg.kanban.managers.Managers;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

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

        //taskManager.getTaskById(taskSound.getId());
        taskManager.getTaskById(500);
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

        System.out.println("Удаляем задачу " + taskSound.getId());
        taskManager.deleteTask(taskSound);
        printHistory(taskManager);

        System.out.println("Удаляем эпик " + epicSDK.getId());
        taskManager.deleteTask(epicSDK);

        taskManager.clearAllTasks();
        taskManager.clearAllSubtasks();
        taskManager.clearAllEpics();
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        //manager.getHistory().forEach(System.out::println);
        System.out.println(manager.getHistory());
    }
}
