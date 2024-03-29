package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);

    List<Task> getHistory();
}
