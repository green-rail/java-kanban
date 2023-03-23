package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Task> getAllEpics();

    List<Task> getAllSubtasks();

    void clearAllTasks();
    void clearAllEpics();
    void clearAllSubtasks();

    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    int addTask(Task task);

    boolean updateTask(Task task);
    void deleteTask(Task task);
    List<Task> getHistory();
    Iterator<Task> getPrioritizedTasks();
}
