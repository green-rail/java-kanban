package ru.smg.kanban.tasks;

public class Task {

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    protected TaskType taskType;
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }
    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    protected String getToStringPrefix(){
        return "[" + getId() + " task] ";
    }

    @Override
    public String toString() {
        return getToStringPrefix() + getName() + "(" + getDescription() + ") [" + getStatus() + "]";
    }

    public String serialize() {
        return String.format("%s,%s,%s,%s,%s", id, taskType, name, status, description);
    }
}
