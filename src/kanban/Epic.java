package kanban;

import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
    private final ArrayList<Subtask> subtasks;

    Epic(long id, String name, String description, ArrayList<Subtask> subtasks) {
        super(id, name, description, Status.NEW);
        this.subtasks = subtasks;
    }

    @Override
    public Status getStatus(){
        if (subtasks.size() == 0){
            return Status.NEW;
        }
        boolean isDone = true;
        boolean isNew = true;
        for (Subtask subtask: subtasks) {
            if (subtask.getStatus() != Status.NEW){
                isNew = false;
            }
            if (subtask.getStatus() != Status.DONE){
                isDone = false;
            }
        }
        if (isNew){
            return Status.NEW;
        } else if (isDone){
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

}
