package ru.smg.kanban.managers;

import ru.smg.kanban.kvserver.KVTaskClient;
import ru.smg.kanban.tasks.Task;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {

    private String url;
    private KVTaskClient client;

    public HttpTaskManager(HistoryManager manager, String url) {
        super(manager, url);
    }

    public static HttpTaskManager restoreFromServer(String url) {
        var historyManager = Managers.getDefaultHistory();
        var manager = new HttpTaskManager(historyManager, url);
        manager.load();
        return manager;
    }

    @Override
    protected void initStorage(String url) {
        this.url = url;
        try {
            client = new KVTaskClient(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void load() {
        String content;
        try {
            content = client.load("save");
        } catch (IOException | InterruptedException e ) {
            throw new RuntimeException(e);
        }


        var contents = content.split("\n");
        if (contents.length < 2) {
            return;
        }
        int i = 1;
        String line = contents[i];
        while (!line.isBlank()) {
            var task = fromString(line);
            if (task != null) {
                //super.addTask(task);
                super.insertTask(task);
            }
            i++;
            if (i >= contents.length){
                return;
            }
            line = contents[i];
        }
        String historyLine = contents[contents.length - 1];
        if (historyLine == null || historyLine.isBlank()) {
            return;
        }
        var history = historyFromString(historyLine);
        if (history.isEmpty()) {
            return;
        }
        for (int id : history) {
            var task = getTask(id);
            if (task != null) {
                historyManager.add(task);
            }
        }
    }

    @Override
    protected void save() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstLine);
        for (Task task : getAllTasks()) {
            sb.append(task.serialize()).append("\n");
        }
        for (Task epic : getAllEpics()) {
            sb.append(epic.serialize()).append("\n");
        }
        for (Task subtask : getAllSubtasks()) {
            sb.append(subtask.serialize()).append("\n");
        }
        sb.append("\n");
        for (Task task : getHistory()) {
            sb.append(task.getId()).append(",");
        }
        try {
            client.put("save", sb.toString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
