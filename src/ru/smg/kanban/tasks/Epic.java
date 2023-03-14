package ru.smg.kanban.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    private final ArrayList<Subtask> subtasks;

    public Epic(int id, String name, String description, ArrayList<Subtask> subtasks) {
        super(id, name, description, Status.NEW);
        this.subtasks = subtasks;
        taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
        taskType = TaskType.EPIC;
    }

    @Override
    public Status getStatus() {
        if (subtasks.isEmpty()) return Status.NEW;

        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE);
        boolean allNew =  subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW);
        return allDone ? Status.DONE : allNew ? Status.NEW : Status.IN_PROGRESS;
    }

    @Override
    protected String getToStringPrefix() {
        return "[" + getId() + " epic] ";
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
