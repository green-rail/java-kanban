package ru.smg.kanban.managers;

import org.junit.jupiter.api.Test;
import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {

    protected T taskManager;

    @Test
    void getAllTasks() {
        var allTasks = taskManager.getAllTasks();
        assertNotNull(allTasks, "Получение всех задач должно всегда возвращать значение.");
        assertTrue(allTasks.isEmpty(), "Список задач не пуст." );

        var taskList = List.of(
                new Task("Task 1", "Description", Status.NEW),
                new Task("Task 2", "Description", Status.NEW));

        taskManager.addTask(taskList.get(0));
        taskManager.addTask(taskList.get(1));

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertIterableEquals(taskList, tasks, "Задачи не совпадают.");
    }

    @Test
    void getAllEpics() {
        var allEpics = taskManager.getAllEpics();
        assertNotNull(allEpics, "Задачи не возвращаются.");
        assertTrue(allEpics.isEmpty(), "Список задач не пуст." );

        var epic = new Epic("Epic 1", "Description", new ArrayList<>());
        epic.getSubtasks().add(new Subtask("Subtask 1", "Description", Status.DONE, epic));
        taskManager.addTask(epic);

        final List<Task> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
        assertEquals(Status.DONE, epics.get(0).getStatus(), "Неверно определен статус.");
    }

    @Test
    void getAllSubtasks() {
        var allSubtasks = taskManager.getAllSubtasks();
        assertNotNull(allSubtasks, "Задачи не возвращаются.");
        assertTrue(allSubtasks.isEmpty(), "Список задач не пуст." );

        var epic = new Epic("Epic 1", "Description", new ArrayList<>());
        taskManager.addTask(epic);
        var subtask = new Subtask("Subtask 1", "Description", Status.DONE, epic);
        taskManager.addTask(subtask);

        final List<Task> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
        assertNotNull(subtask.getHolder(), "Эпик не возвращается.");
        assertEquals(subtask.getHolder(), epic, "Неверный эпик подзадачи.");
    }

    @Test
    void clearAllTasks() {
        var task = new Task("Task 1", "Description", Status.NEW);
        taskManager.addTask(task);
        taskManager.clearAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не очищаются");
    }

    @Test
    void clearAllEpics() {
        var epic = new Epic("Epic 1", "Description", new ArrayList<>());
        taskManager.addTask(epic);
        var subtask = new Subtask("Subtask 1", "Description", Status.DONE, epic);
        taskManager.addTask(subtask);
        taskManager.clearAllEpics();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не очищаются.");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не очищаются.");
    }

    @Test
    void clearAllSubtasks() {
        var epic = new Epic("Epic 1", "Description", new ArrayList<>());
        taskManager.addTask(epic);
        var subtask = new Subtask("Subtask 1", "Description", Status.DONE, epic);
        taskManager.addTask(subtask);
        taskManager.clearAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не очищаются.");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпики очищаются вместе с подзадачами.");
    }

    @Test
    void getTaskById() {
        assertNull(taskManager.getTaskById(0), "Список задач должен быть пуст.");

        var task = new Task("Task 1", "Description", Status.NEW);
        int index = taskManager.addTask(task);
        assertNotNull(taskManager.getTaskById(index), "Задача не возвращается.");
        assertEquals(task, taskManager.getTaskById(index), "Вернулась не та задача.");
        assertNull(taskManager.getTaskById(500), "Вернул задачу по несуществующему идентификатору");
    }

    @Test
    void getEpicById() {
        assertNull(taskManager.getEpicById(0), "Список задач должен быть пуст.");

        var epic = new Epic("Epic 1", "Description", new ArrayList<>());
        int index = taskManager.addTask(epic);
        assertNotNull(taskManager.getEpicById(index), "Задача не возвращается.");
        assertEquals(epic, taskManager.getEpicById(index), "Вернулась не та задача.");
        assertEquals(Status.NEW, taskManager.getEpicById(index).getStatus(), "Неверно определен статус.");
        assertNull(taskManager.getEpicById(500), "Вернул задачу по несуществующему идентификатору.");
    }

    @Test
    void getSubtaskById() {
        assertNull(taskManager.getSubtaskById(0), "Список задач должен быть пуст.");

        var epic = new Epic("Epic 1", "Description", new ArrayList<>());
        var subtask = new Subtask("Subtask 1", "Description", Status.NEW, epic);
        int index = taskManager.addTask(subtask);
        assertNotNull(taskManager.getSubtaskById(index), "Задача не возвращается.");
        assertEquals(subtask, taskManager.getSubtaskById(index), "Вернулась не та задача.");
        assertNull(taskManager.getSubtaskById(500), "Вернул задачу по несуществующему идентификатору.");
    }

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void updateTask() {

        Task task = new Task("Task 1", "Description", Status.NEW);
        final int taskId = taskManager.addTask(task);

        Task newTask = new Task(taskId, task.getName(),  "Updated description", Status.IN_PROGRESS);

        taskManager.updateTask(newTask);

        assertNotNull(taskManager.getTaskById(taskId), "Задача не найдена.");
        assertEquals(newTask, taskManager.getTaskById(taskId), "Задачи не совпадают.");

        Epic epic = new Epic("Epic 1", "Description", new ArrayList<>());
        final int epicId = taskManager.addTask(epic);

        Epic newEpic = new Epic(epicId, epic.getName(),  "Updated description", epic.getSubtasks());

        taskManager.updateTask(newEpic);

        assertNotNull(taskManager.getEpicById(epicId), "Задача не найдена.");
        assertEquals(newEpic, taskManager.getEpicById(epicId), "Задачи не совпадают.");


        Subtask subtask = new Subtask("Subtask 1", "Description", Status.NEW, newEpic);
        final int subtaskId = taskManager.addTask(subtask);

        Subtask newSubtask = new Subtask(subtaskId, subtask.getName(),
                "Updated description", Status.IN_PROGRESS, newEpic);

        taskManager.updateTask(newSubtask);

        assertNotNull(taskManager.getSubtaskById(subtaskId), "Задача не найдена.");
        assertEquals(newSubtask, taskManager.getSubtaskById(subtaskId), "Задачи не совпадают.");
        assertEquals(Status.IN_PROGRESS,
                taskManager.getEpicById(epicId).getStatus(), "Статус эпика не обновился");
    }

    @Test
    void deleteTask() {

        Task task = new Task("Task 1", "Description", Status.NEW);
        assertDoesNotThrow(() -> taskManager.deleteTask(task), "Ошибка при удалении не добавленной задачи.");
        final int taskId = taskManager.addTask(task);
        assertEquals(1, taskManager.getAllTasks().size(), "Задача не добавилась.");
        taskManager.deleteTask(task);

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача не удалилась.");

        Epic epic = new Epic("Epic 1", "Description", new ArrayList<>());
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, epic);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.NEW, epic);
        taskManager.addTask(subtask1);
        final int sub2Id = taskManager.addTask(subtask2);

        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не добавился.");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Подзадачи не добавились.");

        taskManager.deleteTask(subtask2);
        assertNull(taskManager.getSubtaskById(sub2Id), "Задача не удалилась.");

        taskManager.deleteTask(epic);

        assertTrue(taskManager.getAllEpics().isEmpty(), "Задача не удалилась.");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалились вместе с эпиком");
    }


    @Test
    void getHistory() {
        assertNotNull(taskManager.getHistory(), "История не вернулась.");
        Task task = new Task("Task 1", "Description", Status.NEW);
        final int taskId = taskManager.addTask(task);
        taskManager.getTaskById(taskId);

        assertEquals(1, taskManager.getHistory().size(), "История не сохранилась.");
    }
}