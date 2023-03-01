package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Task> getAllEpics();

    ArrayList<Task> getAllSubtasks();

    void clearAllTasks();
    void clearAllEpics();
    void clearAllSubtasks();

    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    int getNextId();

    void addTask(Task task);

    void updateTask(Task task);
    void deleteTask(Task task);
    ArrayList<Subtask> getSubtasks(Epic epic);
    List<Task> getHistory();
}
