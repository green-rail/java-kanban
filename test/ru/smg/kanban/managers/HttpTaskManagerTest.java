package ru.smg.kanban.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.smg.kanban.kvserver.KVServer;
import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest  extends TaskManagerTest<HttpTaskManager>{

    KVServer server;

    private final String baseUrl = "http://localhost:8078/";

    @BeforeEach
    void runServer() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = new HttpTaskManager(Managers.getDefaultHistory(), baseUrl);
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void restoreFromServer() {
        HttpTaskManager restoredManager = HttpTaskManager.restoreFromServer(baseUrl);
        assertNotNull(restoredManager, "Менеджер не восстановился.");

        stopServer();
        runServer();

        Epic epic = new Epic("Epic 1", "Description");
        taskManager.addTask(epic);
        restoredManager = HttpTaskManager.restoreFromServer(baseUrl);
        assertNotNull(restoredManager, "Менеджер не восстановился.");
        assertEquals(1, restoredManager.getAllEpics().size(), "Эпик не восстановился.");
        assertEquals(0, restoredManager.historyManager.getHistory().size(), "История не пуста.");

        stopServer();
        runServer();

        Task task1 = new Task(0, "Task 1", "Description", Status.NEW);
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.now());
        Task task2 = new Task(1,"Task 2", "Description", Status.NEW);
        task2.setDuration(Duration.ofMinutes(45));
        task2.setStartTime(LocalDateTime.now().plusHours(2));
        Epic epic1 = new Epic(2, "Epic 1", "Description");
        Subtask subtask1 = new Subtask(3, "Subtask 1", "Description", Status.NEW, epic1.getId());
        subtask1.setDuration(Duration.ofMinutes(15));
        subtask1.setStartTime(LocalDateTime.now().plusHours(5));

        int task1Index = taskManager.addTask(task1);
        int task2Index = taskManager.addTask(task2);
        int epic1Index = taskManager.addTask(epic1);
        int subtask1Index =  taskManager.addTask(subtask1);

        taskManager.getTaskById(task1Index);
        taskManager.getTaskById(task2Index);
        taskManager.getEpicById(epic1Index);
        taskManager.getSubtaskById(subtask1Index);

        restoredManager = HttpTaskManager.restoreFromServer(baseUrl);

        assertNotNull(restoredManager, "Менеджер не восстановился.");
        assertEquals(2, restoredManager.getAllTasks().size(), "Задачи не восстановились.");
        assertEquals(epic1.getId(), restoredManager.getAllEpics().get(0).getId(), "Эпик не восстановился.");
        assertEquals(subtask1.getId(), restoredManager.getAllSubtasks().get(0).getId(),
                "Подзадача не восстановилась.");
        assertEquals(4, restoredManager.getHistory().size(), "История не восстановилась.");
    }
}