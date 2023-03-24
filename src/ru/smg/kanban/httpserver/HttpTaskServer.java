package ru.smg.kanban.httpserver;

import com.sun.net.httpserver.HttpServer;
import ru.smg.kanban.managers.Managers;
import ru.smg.kanban.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final HttpServer server;

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() {
        try {
            taskManager = Managers.getFileBackedTaskManager();
            server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks/",         new TasksHandler(taskManager));
        server.createContext("/tasks/task/",    new TaskHandler(taskManager));
        server.createContext("/tasks/epic/",    new EpicHandler(taskManager));
        server.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-Task-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
    }
}
