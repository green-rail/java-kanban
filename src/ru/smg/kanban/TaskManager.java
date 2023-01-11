package ru.smg.kanban;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Long, Task> tasks;
    private HashMap<Long, Task> epics;
    private HashMap<Long, Task> subTasks;
    private long nextTaskId = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getAllSubtasks() {
        return (ArrayList<Task>)subTasks.values();
    }

    public void clearAllTasks() {
        tasks.clear();
    }
    public void clearAllEpics() {
        epics.clear();
    }
    public void clearAllSubtasks() {
        subTasks.clear();
    }

    public Task getTaskById(long id) {
        return tasks.get(id);
    }
    public Epic getEpicById(long id) {
        return (Epic)epics.get(id);
    }
    public Subtask getSubtaskById(long id) {
        return (Subtask)subTasks.get(id);
    }

    public void addTask(Task task) {
        if (task == null){
            return;
        }
        if (task instanceof Subtask){
            var subtask = (Subtask)task;
            subtask.getHolder().getSubtasks().add(subtask);
        }
        getHashMap(task).put(task.getId(), task);
    }

    public Task makeTask(String name, String description) {
        return new Task(nextTaskId++, name, description, Task.Status.NEW);
    }
    public Task makeTask(Task task, String name, String description, Task.Status status) {
        return new Task(task.getId(), name, description, status);
    }
    public Epic makeEpic(String name, String description) {
        return new Epic(nextTaskId++, name, description, new ArrayList<>());
    }
    public Epic makeEpic(Epic epic, String name, String description) {
        return new Epic(epic.getId(), name, description, epic.getSubtasks());
    }
    public Subtask makeSubtask(String name, String description, Epic epic) {
        return new Subtask(nextTaskId++, name, description, Task.Status.NEW, epic);
    }
    public Subtask makeSubtask(Subtask subtask, String name, String description, Epic epic, Task.Status status) {
        return new Subtask(subtask.getId(), name, description, status, epic);
    }

    public void updateTask(Task task) {
        var map = getHashMap(task);
        if (task instanceof Subtask){
            var subtask = (Subtask)task;
            var subtasks = subtask.getHolder().getSubtasks();
            for (int i = 0; i < subtasks.size(); i++) {
                if (subtask.getId() == subtasks.get(i).getId()){
                    subtasks.set(i, subtask);
                    break;
                }
            }
        }
        map.put(task.getId(), task);
    }

    public void deleteTask(Task task) {
        var map = getHashMap(task);
        if (task instanceof Epic){
            for (Subtask subtask: ((Epic)task).getSubtasks()) {
                map.remove(subtask.getId());
            }
        }
        if (task instanceof Subtask){
            ((Subtask)task).getHolder().getSubtasks().remove(task);
        }
        map.remove(task.getId());
    }

    public ArrayList<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    private HashMap<Long, Task> getHashMap(Task task) {
        if (task instanceof Epic){
            return epics;
        } else if (task instanceof Subtask) {
            return subTasks;
        }
        return tasks;
    }
}
