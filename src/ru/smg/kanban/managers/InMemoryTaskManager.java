package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Task> epics;
    private final HashMap<Integer, Task> subTasks;
    
    private final TreeSet<Task> sortedTasks;
    protected int nextId = 0;

    public InMemoryTaskManager(HistoryManager manager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = manager;
        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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

        tasks.values().forEach(t -> {
           historyManager.remove(t.getId());
           sortedTasks.remove(t);} );

        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        for (Task epic : epics.values()) {
            ((Epic)epic).getSubtasks().forEach(st -> historyManager.remove(st.getId()));
            historyManager.remove(epic.getId());
        }
        subTasks.clear();
        epics.clear();
        sortedTasks.removeIf(t -> t instanceof Epic || t instanceof Subtask);
    }

    @Override
    public void clearAllSubtasks() {
        for (Task task : epics.values()) {
            var epic = (Epic)task;
            var subtasks = epic.getSubtasks();
            subtasks.forEach(st -> historyManager.remove(st.getId()));
            subtasks.forEach(epic::removeSubtask);
        }
        subTasks.clear();
        sortedTasks.removeIf(t -> t instanceof Subtask);
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
        task.setId(nextId);
        if (!sortedTasks.isEmpty() && sortedTasks.last().getStartTime().isEqual(task.getEndTime())){
            task.setStartTime(task.getStartTime().plusNanos(1));
        }
        if (overlaps(task)) {
            System.out.println("Задача не была добавлена из-за пересечения по времени.");
            return -1;
        }
        if (task instanceof Subtask) {
            var subtask = (Subtask) task;
            subtask.getHolder().addSubtask(subtask);
        }
        getHashMap(task).put(task.getId(), task);
        sortedTasks.add(task);
        nextId++;
        return task.getId();
    }

    private boolean overlaps(Task task) {
        if (task.getDuration().isZero()) return false;
        for (Iterator<Task> it = getPrioritizedTasks(); it.hasNext(); ) {
            var t = it.next();
            if (task.getId() == t.getId()) return false;
            if (task.getStartTime().isAfter(t.getEndTime())) continue;

            if (checkTimeOverlap(t, task)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTimeOverlap(Task task1, Task task2) {
        boolean task1IsBefore = task1.getEndTime().isBefore(task2.getStartTime());
        if (task1IsBefore) return false;

        return !(task1.getStartTime().isAfter(task2.getEndTime()));
    }

    @Override
    public void updateTask(Task task) {
        var map = getHashMap(task);
        if (!map.containsKey(task.getId())) {
            addTask(task);
            return;
        }
        if(overlaps(task)) {
            System.out.println("Задача не была добавлена из-за пересечения по времени.");
            return;
        }
        if (task instanceof Subtask) {
            var subtask = (Subtask) task;
            subtask.getHolder().updateSubtask(subtask);
        }
        map.put(task.getId(), task);
    }

    @Override
    public void deleteTask(Task task) {
        var map = getHashMap(task);
        if (task instanceof Epic) {
            Epic epic = (Epic)task;
            for (Subtask subtask : epic.getSubtasks()) {
                getHashMap(subtask).remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
        }
        if (task instanceof Subtask) {
            var subtask = (Subtask)task;
            subtask.getHolder().removeSubtask(subtask);
        }
        map.remove(task.getId());
        sortedTasks.remove(task);
        historyManager.remove(task.getId());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Iterator<Task> getPrioritizedTasks() {
        return sortedTasks.iterator();
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
