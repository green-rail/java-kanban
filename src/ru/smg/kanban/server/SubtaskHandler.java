package ru.smg.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

class SubtaskHandler extends BaseTaskHandler {

    SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processEndpoint(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint) {

            case SUBTASK_GET_ALL:
                handleGetAll(exchange);
                break;
            case SUBTASK_GET_BY_ID:
                handleGetById(exchange);
                break;
            case SUBTASK_DELETE_ALL:
                handleDeleteAll(exchange);
                break;
            default:
                writeBadRequestResponse(exchange);
                break;
        }

    }

    private void handleGetAll(HttpExchange exchange) throws  IOException {
        writeResponse(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
    }


    private void handleGetById(HttpExchange exchange) throws IOException {
        var subtaskOpt = getOptSubtask(exchange);
        if (subtaskOpt.isPresent()) {
            writeResponse(exchange, gson.toJson(subtaskOpt.get()), 200);
            return;
        }
        writeResponse(exchange, "Подзадача с таким id не найдена.", 404);
    }
    private Optional<Subtask> getOptSubtask(HttpExchange httpExchange) {
        return getId(httpExchange.getRequestURI().getQuery())
                .map(taskManager::getSubtaskById);
    }

    private void handleDeleteAll(HttpExchange exchange) throws  IOException {
        taskManager.clearAllEpics();
        writeResponse(exchange, "Эпики успешно удалены.", 200);
    }

    private boolean validPath(String[] pathParts) {
        return pathParts.length == 3
                && pathParts[0].isBlank()
                && "tasks".equalsIgnoreCase(pathParts[1])
                && "subtask".equalsIgnoreCase(pathParts[2]);
    }

    @Override
    protected Endpoint getEndpoint(URI uri, String method) {
        //GET; DELETE; /tasks/subtask/   /tasks/subtask/?id=12
        String[] pathParts = uri.getPath().split("/");
        if (!validPath(pathParts)) {
            return Endpoint.UNKNOWN;
        }
        String query = getQuery(uri);

        switch (method) {
            case "GET" :
                if (query.isBlank()) {
                    return Endpoint.SUBTASK_GET_ALL;
                }
                if (getId(query).isPresent()) {
                    return Endpoint.SUBTASK_GET_BY_ID;
                }
                return Endpoint.UNKNOWN;
            case "DELETE" :
                if (query.isBlank()) {
                    return Endpoint.SUBTASK_DELETE_ALL;
                }
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
