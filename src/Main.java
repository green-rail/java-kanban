import ru.smg.kanban.managers.Managers;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        int taskSoundId = taskManager.addTask(new Task(
                "Добавить звук",
                "Звуки ui; выигрыша; проигрыша; фоновая музыка",
                Status.NEW
        ));

        int taskUIId = taskManager.addTask( new Task(
                "Адаптивный ui",
                "Добавить поддержку портретного и горизонтального режимов экрана",
                Status.NEW
        ));

        int epicSDKId = taskManager.addTask( new Epic(
                "SDK Яндекс игр",
                "Интегрировать SDK Яндекс игр"
        ));

        int subSDK1Id = taskManager.addTask( new Subtask(
                "Изучить документацию",
                "За последнее время SDK обновился нужно быть в курсе изменений",
                Status.NEW,
                taskManager.getEpicById(epicSDKId)
        ));
        int subSDK2Id = taskManager.addTask( new Subtask(
                "Лидерборд",
                "Оценить сложность реализации. Если сложно то целесообразность фичи под вопросом",
                Status.NEW,
                taskManager.getEpicById(epicSDKId)
        ));

        int subSDK3 = taskManager.addTask(new Subtask(
                "Облачные сейвы",
                "Добавить сохранение прогресса",
                Status.NEW,
                taskManager.getEpicById(epicSDKId)
        ));

        int epicAsyncId = taskManager.addTask(new Epic(
                "Асинхронный код",
                "Всё что касается асинхронного кода"
        ));


        //taskManager.getTaskById(taskSound.getId());
        taskManager.getTaskById(500);
        taskManager.getTaskById(taskSoundId);
        printHistory(taskManager);

        taskManager.getSubtaskById(subSDK1Id);
        taskManager.getSubtaskById(subSDK2Id);
        printHistory(taskManager);

        taskManager.getTaskById(taskUIId);
        taskManager.getEpicById(epicSDKId);
        taskManager.getTaskById(taskUIId);
        taskManager.getEpicById(epicSDKId);
        printHistory(taskManager);

        //System.out.println("Удаляем задачу " + taskSound.getId());
        //taskManager.deleteTask(taskSound);
        //printHistory(taskManager);

        System.out.println("Удаляем эпик " + epicSDKId);
        taskManager.deleteTask(taskManager.getEpicById(epicSDKId));
        printHistory(taskManager);

        taskManager.clearAllTasks();
        taskManager.clearAllSubtasks();
        taskManager.clearAllEpics();
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        System.out.println(manager.getHistory());
    }
}
