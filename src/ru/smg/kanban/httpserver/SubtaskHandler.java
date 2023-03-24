package ru.smg.kanban.httpserver;

import com.sun.net.httpserver.HttpExchange;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.Epic;
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
            case SUBTASK_GET_FOR_EPIC:
                handleGetForEpic(exchange);
            default:
                writeBadRequestResponse(exchange);
                break;
        }
    }

    private void handleGetForEpic(HttpExchange exchange) throws IOException {
        var epicOpt = getOptEpic(exchange);
        if (epicOpt.isPresent()) {
            writeResponse(exchange, gson.toJson(epicOpt.get().getSubtasks()), 200);
            return;
        }
        writeResponse(exchange, "Эпик с таким id не найден.", 404);
    }
    private Optional<Epic> getOptEpic(HttpExchange httpExchange) {
        return getId(httpExchange.getRequestURI().getQuery())
                .map(taskManager::getEpicById);
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
        taskManager.clearAllSubtasks();
        writeResponse(exchange, "Подзадачи успешно удалены.", 200);
    }

    private boolean isSubtaskCall(String path) {
        return "/tasks/subtask/".equals(path);
    }
    private boolean isEpicCall(String path) {
        return "/tasks/subtask/epic/".equals(path);
    }

    @Override
    protected Endpoint getEndpoint(URI uri, String method) {
        boolean isSubtaskCall = isSubtaskCall(uri.getPath());
        boolean isEpicCall    = isEpicCall(uri.getPath());

        if (!(isSubtaskCall || isEpicCall)) {
            return Endpoint.UNKNOWN;
        }

        String query = getQuery(uri);

        switch (method) {
            case "GET" :
                if (query.isBlank()) {
                    return isSubtaskCall ? Endpoint.SUBTASK_GET_ALL : Endpoint.UNKNOWN;
                }
                if (getId(query).isPresent()) {
                    return isSubtaskCall ? Endpoint.SUBTASK_GET_BY_ID : Endpoint.SUBTASK_GET_FOR_EPIC;
                }
                return Endpoint.UNKNOWN;
            case "DELETE" :
                if (query.isBlank() && isSubtaskCall) {
                    return Endpoint.SUBTASK_DELETE_ALL;
                }
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
