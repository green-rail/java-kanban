package ru.smg.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Task> getAllEpics();

    ArrayList<Task> getAllSubtasks();

    void clearAllTasks();
    void clearAllEpics();
    void clearAllSubtasks();

    Task getTaskById(long id);
    Epic getEpicById(long id);
    Subtask getSubtaskById(long id);

    void addTask(Task task);

    Task makeTask(String name, String description);

    Task makeTask(Task task, String name, String description, Status status);
    Epic makeEpic(String name, String description);
    Epic makeEpic(Epic epic, String name, String description);
    Subtask makeSubtask(String name, String description, Epic epic);
    Subtask makeSubtask(Subtask subtask, String name, String description, Epic epic, Status status);

    void updateTask(Task task);

    void deleteTask(Task task);
    ArrayList<Subtask> getSubtasks(Epic epic);

    List<Task> getHistory();
}
