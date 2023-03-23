package ru.smg.kanban.tasks;

public class Subtask extends Task {
    private final int holderId;

    public int getHolderId() {
        return holderId;
    }

    @Override
    protected String getToStringPrefix() {
        return "[" + getId() + " sub] ";
    }

    public Subtask(int id, String name, String description, Status status, int holderId) {
        super(id, name, description, status);
        this.holderId = holderId;
        taskType = TaskType.SUBTASK;
    }
    public Subtask(String name, String description, Status status, int holderId) {
        super(name, description, status);
        this.holderId = holderId;
        taskType = TaskType.SUBTASK;
    }

    @Override
    public String serialize() {
        return super.serialize() + "," + holderId;
    }
}
