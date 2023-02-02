package ru.smg.kanban.tasks;

public class Task {

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    private final int id;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private String name;
    private String description;
    private Status status;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    protected String getToStringPrefix(){
        return "[" + getId() + " task] ";
    }

    @Override
    public String toString() {
        return getToStringPrefix() + getName() + "(" + getDescription() + ") [" + getStatus() + "]";
    }
}
