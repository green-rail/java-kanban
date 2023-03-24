import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.smg.kanban.httpserver.HttpTaskServer;
import ru.smg.kanban.kvserver.KVServer;
import ru.smg.kanban.managers.Managers;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.httpserver.DateTimeTypeAdapter;
import ru.smg.kanban.httpserver.DurationTypeAdapter;
import ru.smg.kanban.tasks.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {


        //serializeTasks();

        //new KVServer().start();

        //TaskManager taskManager = Managers.getDefault();

        new HttpTaskServer().start();

        //try {
        //    client = new KVTaskClient("http://localhost:8078/");
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //}

        //try {
        //    client.put("0", "my value");
        //    System.out.println("GOT VALUE: " + client.load("0"));
        //    client.put("0", "my updated value");
        //    System.out.println("GOT VALUE: " + client.load("0"));
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //}


        //var server = new HttpTaskServer();
        //server.start();



        /*

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
                epicSDKId
        ));
        int subSDK2Id = taskManager.addTask( new Subtask(
                "Лидерборд",
                "Оценить сложность реализации. Если сложно то целесообразность фичи под вопросом",
                Status.NEW,
                epicSDKId
        ));

        int subSDK3 = taskManager.addTask(new Subtask(
                "Облачные сейвы",
                "Добавить сохранение прогресса",
                Status.NEW,
                epicSDKId
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


         */

    }

    private static void serializeTasks() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class, new DateTimeTypeAdapter());
        builder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        Gson gson = builder.create();

        TaskManager taskManager = Managers.getDefault();
        int index1 = taskManager.addTask(new Task("Task 1", "Description", Status.NEW));
        int index2 = taskManager.addTask(new Task("Task 2", "Description", Status.NEW));
        int epicIndex1 = taskManager.addTask(new Epic("Epic 1", "Description"));
        int subIndex1 = taskManager.addTask(new Subtask("Subtask 1", "Description", Status.NEW, epicIndex1));
        int subIndex2 = taskManager.addTask(new Subtask("Subtask 2", "Description", Status.IN_PROGRESS, epicIndex1));

        String json1 = gson.toJson(taskManager.getTaskById(index1));
        String json2 = gson.toJson(taskManager.getTaskById(index2));
        String jsonEpic = gson.toJson(taskManager.getEpicById(epicIndex1));
        String jsonSub1 = gson.toJson(taskManager.getSubtaskById(subIndex1));
        String jsonSub2 = gson.toJson(taskManager.getSubtaskById(subIndex2));



        System.out.println(json1);
        System.out.println(json2);
        System.out.println(jsonEpic);
        System.out.println(jsonSub1);
        System.out.println(jsonSub2);


        var task1 = gson.fromJson(json1, Task.class);
        var task2 = gson.fromJson(json2, Task.class);
        var epic1 = gson.fromJson(jsonEpic, Epic.class);
        var sub1 = gson.fromJson(jsonSub1, Subtask.class);
        var sub2 = gson.fromJson(jsonSub2, Subtask.class);
        System.out.println("SOMETHING");
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        System.out.println(manager.getHistory());
    }
}
