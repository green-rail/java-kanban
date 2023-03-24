package ru.smg.kanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.smg.kanban.managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

abstract class BaseTaskHandler implements HttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new DateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    protected final TaskManager taskManager;

    BaseTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());
        processEndpoint(exchange, endpoint);
    }

    protected abstract void processEndpoint(HttpExchange exchange, Endpoint endpoint) throws  IOException;

    protected abstract Endpoint getEndpoint(URI uri, String method);

    protected enum Endpoint {
        TASK_GET_ALL,
        TASK_GET_BY_ID,
        TASK_POST_ADD,
        TASK_DELETE_BY_ID,
        TASK_DELETE_ALL,
        EPIC_GET_ALL,
        EPIC_GET_BY_ID,
        EPIC_DELETE_ALL,
        SUBTASK_GET_ALL,
        SUBTASK_GET_BY_ID,
        SUBTASK_DELETE_ALL,
        SUBTASK_GET_FOR_EPIC,
        GET_HISTORY,
        GET_PRIORITIZED,
        UNKNOWN
    }

    protected static Optional<Integer> getId(String idPart) {
        int equalsIndex = idPart.indexOf("=");
        if (equalsIndex < 0) {
            return Optional.empty();
        }
        String numericPart = idPart.substring(equalsIndex + 1);
        try {
            int id = Integer.parseInt(numericPart);
            if (id >= 0) {
                return Optional.of(id);
            }
        } catch (NumberFormatException ignored) {
        }
        return Optional.empty();
    }

    protected static void writeResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {

        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    protected static void writeBadRequestResponse(HttpExchange exchange) throws IOException {
        byte[] bytes = "Некорректный запрос.".getBytes(DEFAULT_CHARSET);
        exchange.sendResponseHeaders(400, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }

    protected static String getQuery(URI uri) {
        var query = uri.getQuery();
        return query == null ? "" : query;
    }
}
