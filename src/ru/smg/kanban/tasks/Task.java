package ru.smg.kanban.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private final int id;
    private String name;
    private String description;
    private Status status;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected TaskType taskType;

    public int getId() {return id;
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



    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Task(String name, String description, Status status) {
        this.id = -1;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
    }

    protected String getToStringPrefix(){
        return "[" + getId() + " task] ";
    }

    @Override
    public String toString() {
        return getToStringPrefix() + getName() + "(" + getDescription() + ") [" + getStatus() + "]";
    }

    public String serialize() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, taskType, name, status, description,duration.toMinutes(),
                startTime.format(formatter));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name) && description.equals(task.description) && status == task.status && duration.equals(task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration);
    }
}
