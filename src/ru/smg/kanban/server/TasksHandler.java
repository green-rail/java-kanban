package ru.smg.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TasksHandler extends BaseTaskHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processEndpoint(HttpExchange exchange, BaseTaskHandler.Endpoint endpoint) throws IOException {
        switch (endpoint) {
            case GET_HISTORY:
                writeResponse(exchange, gson.toJson(taskManager.getHistory()) , 200);
                break;
            case GET_PRIORITIZED:
                processPrioritized(exchange);
                break;
            case UNKNOWN:
                writeBadRequestResponse(exchange);
                break;
        }
    }

    private void processPrioritized(HttpExchange exchange) throws IOException {
        List<Task> tasks = new ArrayList<>();
        Iterator<Task> iterator = taskManager.getPrioritizedTasks();
        while (iterator.hasNext()) {
            tasks.add(iterator.next());
        }
        writeResponse(exchange, gson.toJson(tasks), 200);
    }

    private boolean validPath(String[] pathParts) {
        if (pathParts.length < 2 || pathParts.length > 3) {
            return false;
        }
        boolean baseValid = pathParts[0].isBlank() && "tasks".equalsIgnoreCase(pathParts[1]);
        if (pathParts.length == 2) {
            return baseValid;
        }
        return baseValid && "history".equalsIgnoreCase(pathParts[2]);
    }

    @Override
    protected Endpoint getEndpoint(URI uri, String method) {
        if (!"GET".equalsIgnoreCase(method)) {
            return Endpoint.UNKNOWN;
        }
        String[] pathParts = uri.getPath().split("/");
        String query = getQuery(uri);
        if (!validPath(pathParts) || !query.isBlank()) {
            return Endpoint.UNKNOWN;
        }

        return pathParts.length == 2 ?
                Endpoint.GET_PRIORITIZED :
                Endpoint.GET_HISTORY;
    }
}
