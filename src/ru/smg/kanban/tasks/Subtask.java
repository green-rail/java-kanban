package ru.smg.kanban.tasks;

public class Subtask extends Task {

    private final Epic holder;

    public Epic getHolder() {
        return holder;
    }
    @Override
    protected String getToStringPrefix() {
        return "[" + getId() + " sub] ";
    }

    public Subtask(int id, String name, String description, Status status, Epic holder) {
        super(id, name, description, status);
        this.holder = holder;
        taskType = TaskType.SUBTASK;
    }
    public Subtask(String name, String description, Status status, Epic holder) {
        super(name, description, status);
        this.holder = holder;
        taskType = TaskType.SUBTASK;
    }

    @Override
    public String serialize() {
        return super.serialize() + "," + holder.getId();
    }
}
