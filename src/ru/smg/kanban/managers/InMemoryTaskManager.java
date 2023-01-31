package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Task> epics;
    private final HashMap<Integer, Task> subTasks;
    private int nextTaskId = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager manager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = manager;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        return (ArrayList<Task>) subTasks.values();
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
    }

    @Override
    public void clearAllSubtasks() {
        epics.values().forEach(e -> ((Epic) e).getSubtasks().clear());
        subTasks.clear();
    }

    private void logHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public Task getTaskById(int id) {
        var task = tasks.get(id);
        logHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        var task = epics.get(id);
        logHistory(task);
        return (Epic) task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        var task = subTasks.get(id);
        logHistory(task);
        return (Subtask) task;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (task instanceof Subtask) {
            var subtask = (Subtask) task;
            subtask.getHolder().getSubtasks().add(subtask);
        }
        getHashMap(task).put(task.getId(), task);
    }

    @Override
    public Task makeTask(String name, String description) {
        return new Task(nextTaskId++, name, description, Status.NEW);
    }

    @Override
    public Task makeTask(Task task, String name, String description, Status status) {
        return new Task(task.getId(), name, description, status);
    }

    @Override
    public Epic makeEpic(String name, String description) {
        return new Epic(nextTaskId++, name, description, new ArrayList<>());
    }

    @Override
    public Epic makeEpic(Epic epic, String name, String description) {
        return new Epic(epic.getId(), name, description, epic.getSubtasks());
    }

    @Override
    public Subtask makeSubtask(String name, String description, Epic epic) {
        return new Subtask(nextTaskId++, name, description, Status.NEW, epic);
    }

    @Override
    public Subtask makeSubtask(Subtask subtask, String name, String description, Epic epic, Status status) {
        return new Subtask(subtask.getId(), name, description, status, epic);
    }

    @Override
    public void updateTask(Task task) {
        var map = getHashMap(task);
        if (task instanceof Subtask) {
            var subtask = (Subtask) task;
            var subtasks = subtask.getHolder().getSubtasks();
            for (int i = 0; i < subtasks.size(); i++) {
                if (subtask.getId() == subtasks.get(i).getId()) {
                    subtasks.set(i, subtask);
                    break;
                }
            }
        }
        map.put(task.getId(), task);
    }

    @Override
    public void deleteTask(Task task) {
        var map = getHashMap(task);
        if (task instanceof Epic) {
            for (Subtask subtask : ((Epic) task).getSubtasks()) {
                map.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
        }
        if (task instanceof Subtask) {
            ((Subtask) task).getHolder().getSubtasks().remove(task);
        }
        map.remove(task.getId());
        historyManager.remove(task.getId());
    }

    @Override
    public ArrayList<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private HashMap<Integer, Task> getHashMap(Task task) {
        if (task instanceof Epic) {
            return epics;
        } else if (task instanceof Subtask) {
            return subTasks;
        }
        return tasks;
    }
}
