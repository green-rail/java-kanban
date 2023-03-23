package ru.smg.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final String fileName = "testSaveFile.csv";

    private File saveFile;

    private void clearSaveFile() {
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveFile = new File(fileName);
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void makeManager() {
        clearSaveFile();
        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), saveFile);
    }


    @Test
    void testRestoreState() {
        FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(saveFile);
        assertNotNull(restoredManager, "Менеджер не восстановился.");

        clearSaveFile();

        Epic epic = new Epic("Epic 1", "Description");
        taskManager.addTask(epic);
        restoredManager = FileBackedTaskManager.loadFromFile(saveFile);
        assertNotNull(restoredManager, "Менеджер не восстановился.");
        assertEquals(1, restoredManager.getAllEpics().size(), "Эпик не восстановился.");

        assertEquals(0, restoredManager.historyManager.getHistory().size(), "История не пуста.");

        makeManager();

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

        restoredManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertNotNull(restoredManager, "Менеджер не восстановился.");
        assertEquals(2, restoredManager.getAllTasks().size(), "Задачи не восстановились.");
        assertEquals(epic1.getId(), restoredManager.getAllEpics().get(0).getId(), "Эпик не восстановился.");
        assertEquals(subtask1.getId(), restoredManager.getAllSubtasks().get(0).getId(),
                "Подзадача не восстановилась.");
        assertEquals(4, restoredManager.getHistory().size(), "История не восстановилась.");
    }
}