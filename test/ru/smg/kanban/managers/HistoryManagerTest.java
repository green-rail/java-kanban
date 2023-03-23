package ru.smg.kanban.managers;

import org.junit.jupiter.api.Test;
import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest <T extends HistoryManager> {

    protected T historyManager;

    @Test
    void add() {
        Task task = new Task("Task 1", "Description", Status.NEW);
        historyManager.add(task);

        var history = historyManager.getHistory();
        assertNotNull(history, "История не вернулась.");
        assertEquals(1, history.size(), "История не сохранилась.");

        historyManager.add(task);
        assertEquals(1, history.size(), "Дублирующаяся запись в истории");
    }

    @Test
    void remove() {
        assertDoesNotThrow(() -> historyManager.remove(0), "Ошибка при удалении несуществующей задачи.");

        List<Task> tasks = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            tasks.add(new Task(i, "Task " + i, "Description", Status.NEW));
        }
        tasks.forEach(historyManager::add);

        assertIterableEquals(tasks, historyManager.getHistory(), "История не сохранилась.");

        historyManager.remove(0);
        tasks.remove(0);
        assertIterableEquals(tasks, historyManager.getHistory(), "Удалена не та задача.");

        historyManager.remove(2);
        tasks.remove(1);
        assertIterableEquals(tasks, historyManager.getHistory(), "Удалена не та задача.");

        historyManager.remove(3);
        tasks.remove(1);
        assertIterableEquals(tasks, historyManager.getHistory(), "Удалена не та задача.");

        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size(), "Задачи не удалились.");

        Epic epic = new Epic(0, "Epic 1", "Description");
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description", Status.NEW, epic.getId());
        epic.addSubtask(subtask1);
        historyManager.add(epic);
        historyManager.add(subtask1);
        assertEquals(2, historyManager.getHistory().size(), "История не сохранилась.");
        historyManager.remove(0);
        assertEquals(0, historyManager.getHistory().size(), "Подзадача не удалилась вместе с эпиком");
    }

    @Test
    void getHistory() {
        var history = historyManager.getHistory();
        assertNotNull(history, "История не вернулась.");

    }
}