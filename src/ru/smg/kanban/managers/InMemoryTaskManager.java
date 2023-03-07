package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Status;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Task> epics;
    private final HashMap<Integer, Task> subTasks;
    protected int nextId = 0;

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
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.values().forEach(t -> historyManager.remove(t.getId()));
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        for (Task epic : epics.values()) {
            ((Epic)epic).getSubtasks().forEach(st -> historyManager.remove(st.getId()));
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void clearAllSubtasks() {
        for (Task epic : epics.values()) {
            var subtasks = ((Epic) epic).getSubtasks();
            subtasks.forEach(st -> historyManager.remove(st.getId()));
            subtasks.clear();
        }
        subTasks.clear();
    }

    protected void logHistory(Task task) {
        if (task != null) {
            historyManager.add(task);
        }
    }

    protected Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return subTasks.getOrDefault(id, null);
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
    public int addTask(Task task) {
        if (task == null) {
            return -1;
        }
        task.setId(nextId++);
        if (task instanceof Subtask) {
            var subtask = (Subtask) task;
            subtask.getHolder().getSubtasks().add(subtask);
        }
        getHashMap(task).put(task.getId(), task);
        return task.getId();
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
