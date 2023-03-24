package ru.smg.kanban.httpserver;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer server;
    private static final String baseUrl = "http://localhost:8080/tasks/";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = BaseTaskHandler.gson;

    @BeforeEach
    void startServer() {
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    private HttpResponse<String> send(URI uri, String method, String content) throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(content);
        var builder = HttpRequest.newBuilder();
        builder.uri(uri);
        switch (method) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(body);
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                builder.GET();
                break;
        }
        HttpRequest request = builder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void taskEndpoints() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl + "task/");
        var response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals("[]", response.body(), "Пустая коллекция не вернулась.");

        Task task0 = new Task(0, "Task 1", "Description", Status.NEW);
        String json = gson.toJson(task0);
        response = send(url, "POST", json);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");

        url = URI.create(baseUrl+ "task/?id=0");
        response = send(url, "GET", "");
        Task retrievedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals(task0, retrievedTask, "Задача не совпадает.");

        url = URI.create(baseUrl+ "task/");
        Task task1 = new Task(1, "Task 2", "Description", Status.NEW);
        response = send(url, "POST", gson.toJson(task1));
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");

        var tasks = List.of(task0, task1);

        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");

        JsonArray jArray = JsonParser.parseString(response.body()).getAsJsonArray();
        List<Task> retrievedTasks = new ArrayList<>();
        for (JsonElement obj : jArray) {
            retrievedTasks.add(gson.fromJson(obj.toString(), Task.class));
        }
        assertIterableEquals(tasks, retrievedTasks, "Задачи не совпадают.");


        url = URI.create(baseUrl+ "task/?id=1");
        response = send(url, "DELETE", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");


        url = URI.create(baseUrl+ "task/");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        tasks = List.of(task0);
        retrievedTasks.clear();
        jArray = JsonParser.parseString(response.body()).getAsJsonArray();
        for (JsonElement obj : jArray) {
            retrievedTasks.add(gson.fromJson(obj.toString(), Task.class));
        }
        assertIterableEquals(tasks, retrievedTasks, "Задачи не совпадают.");

        response = send(url, "DELETE", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");

        response = send(url, "GET", "");
        assertEquals("[]", response.body(), "Пустая коллекция не вернулась.");
    }

    @Test
    void epicEndpoints() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl + "epic/");
        var response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals("[]", response.body(), "Пустая коллекция не вернулась.");

        url = URI.create(baseUrl + "task/");
        Epic epic0 = new Epic(0, "Epic 1", "Description");
        String json = gson.toJson(epic0);
        response = send(url, "POST", json);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");

        url = URI.create(baseUrl+ "epic/?id=0");
        response = send(url, "GET", "");
        Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals(epic0, retrievedEpic, "Задача не совпадает.");

        url = URI.create(baseUrl+ "epic/");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");

        String rawEpic = JsonParser.parseString(response.body()).getAsJsonArray().get(0).toString();

        retrievedEpic = gson.fromJson(rawEpic, Epic.class);
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals(epic0, retrievedEpic, "Задача не совпадает.");

        url = URI.create(baseUrl+ "epic/");
        response = send(url, "DELETE", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals("[]", response.body(), "Код ответа не совпадает.");
    }

    @Test
    void subtaskEndpoints()  throws IOException, InterruptedException {
        URI url = URI.create(baseUrl + "subtask/");
        var response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals("[]", response.body(), "Пустая коллекция не вернулась.");

        Epic epic0 = new Epic(0, "Epic 0", "Description");
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description", Status.NEW, 0);

        url = URI.create(baseUrl + "task/");
        String json = gson.toJson(subtask1);
        response = send(url, "POST", json);
        assertEquals(400, response.statusCode(), "Код ответа не совпадает.");

        json = gson.toJson(epic0);
        response = send(url, "POST", json);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");
        json = gson.toJson(subtask1);
        response = send(url, "POST", json);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");


        url = URI.create(baseUrl+ "subtask/?id=1");
        response = send(url, "GET", "");
        Subtask retrievedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals(subtask1, retrievedSubtask, "Задача не совпадает.");

        url = URI.create(baseUrl+ "subtask/");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        String rawSubtask = JsonParser.parseString(response.body()).getAsJsonArray().get(0).toString();
        retrievedSubtask = gson.fromJson(rawSubtask, Subtask.class);
        assertEquals(subtask1, retrievedSubtask, "Задача не совпадает.");

        response = send(url, "DELETE", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals("[]", response.body(), "Пустая коллекция не вернулась.");

        url = URI.create(baseUrl+ "task/");
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description", Status.NEW, 0);
        json = gson.toJson(subtask2);
        response = send(url, "POST", json);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");

        url = URI.create(baseUrl+ "subtask/epic/?id=0");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        rawSubtask = JsonParser.parseString(response.body()).getAsJsonArray().get(0).toString();
        retrievedSubtask = gson.fromJson(rawSubtask, Subtask.class);
        assertEquals(subtask2, retrievedSubtask, "Задача не совпадает.");

        url = URI.create(baseUrl+ "epic/");
        response = send(url, "DELETE", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");

        url = URI.create(baseUrl+ "subtask/");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        assertEquals("[]", response.body(), "Подзадачи не удалились вместе с эпиком.");

    }


    @Test
    void history() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl + "task/");

        Task task0 = new Task(0, "Task 0", "Description", Status.NEW);
        String json = gson.toJson(task0);
        HttpResponse<String> response = send(url, "POST", json);
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");

        Task task1 = new Task(1, "Task 1", "Description", Status.NEW);
        response = send(url, "POST", gson.toJson(task1));
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");


        url = URI.create(baseUrl+ "task/?id=1");
        response = send(url, "GET", "");
        url = URI.create(baseUrl+ "task/?id=0");
        response = send(url, "GET", "");


        url = URI.create(baseUrl+ "history/");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");

        var history = List.of(task1, task0);
        JsonArray jArray = JsonParser.parseString(response.body()).getAsJsonArray();
        List<Task> retrievedHistory = new ArrayList<>();
        for (JsonElement obj : jArray) {
            retrievedHistory.add(gson.fromJson(obj.toString(), Task.class));
        }
        assertIterableEquals(history, retrievedHistory, "История не совпадает.");

        url = URI.create(baseUrl+ "task/?id=0");
        response = send(url, "DELETE", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");

        url = URI.create(baseUrl+ "history/");
        response = send(url, "GET", "");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает.");
        history = List.of(task1);
        retrievedHistory.clear();
        jArray = JsonParser.parseString(response.body()).getAsJsonArray();
        for (JsonElement obj : jArray) {
            retrievedHistory.add(gson.fromJson(obj.toString(), Task.class));
        }
        assertIterableEquals(history, retrievedHistory, "История не обновилась.");
    }

    @Test
    void prioritized()  throws IOException, InterruptedException {
        Task task0 = new Task(0, "Task 1", "High priority", Status.NEW);
        Task task1 = new Task(1, "Task 2", "Low priority",  Status.NEW);
        task1.setStartTime(LocalDateTime.now());
        task0.setStartTime(LocalDateTime.now().plusHours(2));
        URI url = URI.create(baseUrl+ "task/");
        HttpResponse<String> response = send(url, "POST", gson.toJson(task0));
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");
        response = send(url, "POST", gson.toJson(task1));
        assertEquals(201, response.statusCode(), "Код ответа не совпадает.");


        var prioritized = List.of(task1, task0);
        url = URI.create(baseUrl);
        response = send(url, "GET", "");
        ArrayList<Task> retrievedPrioritized = new ArrayList<>();
        JsonArray jArray = JsonParser.parseString(response.body()).getAsJsonArray();
        for (JsonElement obj : jArray) {
            retrievedPrioritized.add(gson.fromJson(obj.toString(), Task.class));
        }
        assertIterableEquals(prioritized, retrievedPrioritized, "Приоритеты не совпадают.");
    }
}