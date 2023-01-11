package ru.smg.kanban;

public class Subtask extends Task {

    public Epic getHolder() {
        return holder;
    }
    private final Epic holder;

    Subtask(long id, String name, String description, Status status, Epic holder) {
        super(id, name, description, status);
        this.holder = holder;
    }
}
