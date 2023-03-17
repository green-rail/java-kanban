package ru.smg.kanban.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    private LocalDateTime calculatedStartTime;
    private Duration calculatedDuration;

    @Override
    public LocalDateTime getStartTime() {
        return calculatedStartTime;
    }

    @Override
    public Duration getDuration() {
        return calculatedDuration;
    }

    public List<Subtask> getSubtasks() {
        return List.copyOf(subtasks); //TODO probably should cache it
    }

    public Epic(int id, String name, String description ) {
        super(id, name, description, Status.NEW);
        this.subtasks = new ArrayList<>();
        taskType = TaskType.EPIC;
        updateTimings();
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasks = new ArrayList<>();
        taskType = TaskType.EPIC;
        updateTimings();
    }

    @Override
    public Status getStatus() {
        if (subtasks.isEmpty()) return Status.NEW;

        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE);
        boolean allNew =  subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW);
        return allDone ? Status.DONE : allNew ? Status.NEW : Status.IN_PROGRESS;
    }

    @Override
    protected String getToStringPrefix() {return "[" + getId() + " epic] ";}

    public void addSubtask(Subtask subtask) {

        if (subtasks.contains(subtask)) return;

        subtasks.add(subtask);
        updateTimings();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateTimings();
    }

    public void updateSubtask(Subtask subtask) {

        Subtask oldSubtask = subtasks.stream()
                .filter(sub -> sub.getId() == subtask.getId())
                .findFirst()
                .orElse(null);

        if (oldSubtask == null) return;

        subtasks.remove(oldSubtask);
        subtasks.add(subtask);
        updateTimings();
    }

    private void updateTimings() {
        if (subtasks.isEmpty()) {
            calculatedDuration = Duration.ZERO;
            calculatedStartTime = LocalDateTime.now();
            endTime = LocalDateTime.now();
            return;
        }
        calculatedDuration = subtasks.stream()
                .map(s -> s.duration)
                .reduce(Duration.ZERO, Duration::plus);

        subtasks.sort(Comparator.comparing(Task::getStartTime));
        calculatedStartTime = subtasks.get(0).startTime;
        endTime = subtasks.get(subtasks.size() - 1).getEndTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(super.toString() + "\n");
        for (var s : subtasks) {
            str.append("    ").append(s.toString()).append("\n");
        }
        return str.toString();
    }
}
