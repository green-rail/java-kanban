package ru.smg.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.Epic;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

class EpicHandler extends BaseTaskHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processEndpoint(HttpExchange exchange, BaseTaskHandler.Endpoint endpoint) throws IOException {
        switch (endpoint) {
            case EPIC_GET_ALL:
                handleGetAll(exchange);
                break;
            case EPIC_GET_BY_ID:
                handleGetById(exchange);
                break;
            case EPIC_DELETE_ALL:
                handleDeleteAll(exchange);
                break;
            case UNKNOWN:
                writeBadRequestResponse(exchange);
                break;
        }

    }

    private void handleGetAll(HttpExchange exchange) throws  IOException {
        writeResponse(exchange, gson.toJson(taskManager.getAllEpics()), 200);
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        String query = getQuery(exchange.getRequestURI());
        var idOpt = getId(query);
        if (idOpt.isEmpty()) {
            writeBadRequestResponse(exchange);
            return;
        }
        Epic epic = taskManager.getEpicById(idOpt.get());
        if (epic == null) {
            writeResponse(exchange, "Эпик с таким id не найден.", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(epic), 200);
    }

    private void handleDeleteAll(HttpExchange exchange) throws  IOException {
        taskManager.clearAllEpics();
        writeResponse(exchange, "Эпики успешно удалены.", 200);
    }

    private boolean validPath(String[] pathParts) {
        return pathParts.length == 3
                && pathParts[0].isBlank()
                && "tasks".equalsIgnoreCase(pathParts[1])
                && "epic".equalsIgnoreCase(pathParts[2]);
    }

    @Override
    protected Endpoint getEndpoint(URI uri, String method) {
        //GET; DELETE; /tasks/epic/   /tasks/epic/?id=12
        String[] pathParts = uri.getPath().split("/");
        String query = getQuery(uri);
        if (!validPath(pathParts)) {
            return Endpoint.UNKNOWN;
        }
        switch (method) {
            case "GET" :
                if (query.isBlank()) {
                    return Endpoint.EPIC_GET_ALL;
                }
                return getId(query).isPresent() ?
                        Endpoint.EPIC_GET_BY_ID :
                        Endpoint.UNKNOWN;
            case "DELETE" :
                if (query.isBlank()) {
                    return Endpoint.EPIC_DELETE_ALL;
                }
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
