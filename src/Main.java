import ru.smg.kanban.Task;
import ru.smg.kanban.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
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

        var epicAsync = taskManager.makeEpic("Асинхронный код",
                "Всё что касается асинхронного кода");
        taskManager.addTask(epicAsync);
        var subAsync1 = taskManager.makeSubtask("Async/await в WebGL",
                "Изучить этот вопрос, есть подозрение что придется всё переписать на корутинах", epicAsync);
        taskManager.addTask(subAsync1);

        printTaskManagerContents(taskManager);

        taskManager.updateTask(taskManager.makeTask(taskSound, taskSound.getName(), taskSound.getDescription(),
                Task.Status.DONE));
        taskManager.updateTask(taskManager.makeTask(taskUI, taskUI.getName(), taskUI.getDescription(),
                Task.Status.IN_PROGRESS));

        taskManager.updateTask(taskManager.makeSubtask(subSDK1, subSDK1.getName(), subSDK1.getDescription(),
                subSDK1.getHolder(), Task.Status.IN_PROGRESS));

        taskManager.updateTask(taskManager.makeSubtask(subAsync1, subAsync1.getName(), subAsync1.getDescription(),
                subAsync1.getHolder(), Task.Status.DONE));

        System.out.println("\nСостояние после обновления:");

        printTaskManagerContents(taskManager);

        taskManager.deleteTask(taskSound);
        taskManager.deleteTask(epicAsync);
        taskManager.deleteTask(subSDK2);

        System.out.println("\nСостояние после удаления:");
        printTaskManagerContents(taskManager);
    }

    private static void printTaskManagerContents(TaskManager manager) {
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(t -> System.out.println(t.toString()));
        System.out.println("\nЭпики:");
        manager.getAllEpics().forEach(t -> System.out.println(t.toString()));
    }
}
