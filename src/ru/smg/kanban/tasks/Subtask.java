package ru.smg.kanban.tasks;

public class Subtask extends Task {

    public Epic getHolder() {
        return holder;
    }

    private final Epic holder;

    public Subtask(int id, String name, String description, Status status, Epic holder) {
        super(id, name, description, status);
        this.holder = holder;
    }
}
