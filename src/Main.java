import ru.smg.kanban.Managers;
import ru.smg.kanban.TaskManager;

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

        var epicAsync = taskManager.makeEpic("Асинхронный код",
                "Всё что касается асинхронного кода");
        taskManager.addTask(epicAsync);
        var subAsync1 = taskManager.makeSubtask("Async/await в WebGL",
                "Изучить этот вопрос, есть подозрение что придется всё переписать на корутинах", epicAsync);
        taskManager.addTask(subAsync1);

        taskManager.getTaskById(taskSound.getId());
        taskManager.getTaskById(taskSound.getId());
        taskManager.getEpicById(epicSDK.getId());
        taskManager.getSubtaskById(subSDK1.getId());
        taskManager.getSubtaskById(subSDK2.getId());
        taskManager.getSubtaskById(subAsync1.getId());
        taskManager.getTaskById(taskUI.getId());
        taskManager.getTaskById(taskUI.getId());
        taskManager.getTaskById(taskUI.getId());
        taskManager.getTaskById(taskUI.getId());
        taskManager.getEpicById(epicAsync.getId());

        System.out.println("\nИстория:");
        taskManager.getHistory().forEach(System.out::println);
    }
}
