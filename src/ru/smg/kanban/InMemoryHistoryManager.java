package ru.smg.kanban;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history;

    public InMemoryHistoryManager(){
        history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        history.addLast(task);
        if (history.size() > 10){
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
