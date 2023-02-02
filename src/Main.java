import ru.smg.kanban.managers.Managers;
import ru.smg.kanban.managers.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        var taskSound = taskManager.makeTask("Добавить звук",
                "Звуки ui, выигрыша, проигрыша, фоновая музыка");
        taskManager.addTask(taskSound);
        var taskUI = taskManager.makeTask("Адаптивный ui",
                "Добавить поддержку портретного и горизонтального режимов экрана");
        taskManager.addTask(taskUI);

        var epicSDK = taskManager.makeEpic("SDK Яндекс игр", "Интегрировать SDK Яндекс игр");
        taskManager.addTask(epicSDK);
        var subSDK1 = taskManager.makeSubtask("Изучить документацию",
                "За последнее время SDK обновился, нужно быть в курсе изменений", epicSDK);
        taskManager.addTask(subSDK1);
        var subSDK2 = taskManager.makeSubtask("Лидерборд",
                "Оценить сложность реализации. Если сложно то целесообразность фичи под вопросом", epicSDK);
        taskManager.addTask(subSDK2);
        var subSDK3 = taskManager.makeSubtask("Облачные сейвы",
                "Добавить сохранение прогресса", epicSDK);
        taskManager.addTask(subSDK3);

        var epicAsync = taskManager.makeEpic("Асинхронный код",
                "Всё что касается асинхронного кода");
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
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        //manager.getHistory().forEach(System.out::println);
        System.out.println(manager.getHistory());
    }
}
