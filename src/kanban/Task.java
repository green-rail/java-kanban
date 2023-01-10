package kanban;

public class Task {
    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public long getId() {
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

    private final long id;
    private final String name;
    private final String description;
    private final Status status;

    Task(long id, String name, String description, Status status ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }
}
