package ru.smg.kanban.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.smg.kanban.managers.TaskManager;
import ru.smg.kanban.tasks.Epic;
import ru.smg.kanban.tasks.Subtask;
import ru.smg.kanban.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Optional;


class TaskHandler extends BaseTaskHandler {

    TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processEndpoint(HttpExchange exchange, BaseTaskHandler.Endpoint endpoint) throws IOException {

        switch (endpoint) {
            case TASK_GET_ALL:
                handleGetAll(exchange);
                break;
            case TASK_GET_BY_ID:
                handleGetById(exchange);
                break;
            case TASK_POST_ADD:
                handlePostAddTask(exchange);
                break;
            case TASK_DELETE_BY_ID:
                handleDeleteById(exchange);
                break;
            case TASK_DELETE_ALL:
                handleDeleteAll(exchange);
                break;
            default:
                writeResponse(exchange, "Некорректный запрос.", 400);
        }
    }

    private void handleGetAll(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, gson.toJson(taskManager.getAllTasks()), 200);
    }

    private void handleGetById(HttpExchange httpExchange) throws IOException {
        var taskOpt = getOptTask(httpExchange);
        if (taskOpt.isPresent()) {
            writeResponse(httpExchange, gson.toJson(taskOpt.get()), 200);
            return;
        }
        writeResponse(httpExchange, "Задача с таким id не найдена.", 404);
    }

    private Optional<Task> getOptTask(HttpExchange httpExchange) {
        return getId(httpExchange.getRequestURI().getQuery())
                .map(taskManager::getTaskById);
    }

    private void handlePostAddTask(HttpExchange httpExchange) throws IOException {
        try (InputStream reader = httpExchange.getRequestBody()) {
            String body = new String(reader.readAllBytes(), DEFAULT_CHARSET);
            try {

                JsonObject element = JsonParser.parseString(body).getAsJsonObject();
                String taskType = element.get("taskType").getAsString();

                Type taskClass;
                switch (taskType) {
                    case "EPIC":
                        taskClass = Epic.class;
                        System.out.println("EPIC DETECTED");
                        break;
                    case "SUBTASK":
                        taskClass = Subtask.class;
                        break;
                    default:
                        taskClass = Task.class;
                }

                //Task task = gson.fromJson(body, Task.class);
                Task task = gson.fromJson(body, taskClass);
                if (task.getName().isBlank()) {
                    writeResponse(httpExchange, "Название не может быть пустым.", 400);
                    return;
                }
                boolean success = taskManager.updateTask(task);
                if (success) {
                    writeResponse(httpExchange, "Задача успешно добавлена.", 201);
                } else {
                    writeResponse(httpExchange, "Непредвиденная ошибка при добавлении задачи.", 400);
                }
            } catch (JsonSyntaxException e) {
                writeResponse(httpExchange, "Получен некорректный JSON", 400);
            }
        }
    }

    private void handleDeleteById(HttpExchange httpExchange) throws IOException {
        var idOpt = getId(httpExchange.getRequestURI().getQuery());
        Task task = null;
        if (idOpt.isPresent()) {
            int id = idOpt.get();
            task = taskManager.getTaskById(id);
            if (task == null) {
                task = taskManager.getEpicById(id);
                if (task == null) {
                    task = taskManager.getSubtaskById(id);
                }
            }
        }
        if (task != null) {
            taskManager.deleteTask(task);
            writeResponse(httpExchange, "Задача успешно удалена.", 200);
            return;
        }
        writeResponse(httpExchange, "Задача не найдена.", 404);
    }

    private void handleDeleteAll(HttpExchange httpExchange) throws IOException {
        taskManager.clearAllTasks();
        writeResponse(httpExchange, "Все задачи удалены.", 200);
    }

    private boolean validPath(String[] pathParts) {
        return pathParts.length == 3
                && pathParts[0].isBlank()
                && "tasks".equalsIgnoreCase(pathParts[1])
                && "task".equalsIgnoreCase(pathParts[2]);
    }

    @Override
    protected Endpoint getEndpoint(URI uri, String method) {
        String[] pathParts = uri.getPath().split("/");
        String query = getQuery(uri);
        if (!validPath(pathParts)) {
            return Endpoint.UNKNOWN;
        }
        switch (method) {
            case "GET":
                if (query.isBlank()) {
                    return Endpoint.TASK_GET_ALL;
                }
                return getId(query).isPresent() ?
                        Endpoint.TASK_GET_BY_ID :
                        Endpoint.UNKNOWN;
            case "POST":
                if (query.isBlank()) {
                    return Endpoint.TASK_POST_ADD;
                }
                break;
            case "DELETE":
                if (query.isBlank()) {
                    return Endpoint.TASK_DELETE_ALL;
                } else if (getId(query).isPresent()) {
                    return Endpoint.TASK_DELETE_BY_ID;
                }
                break;
            default:
                return Endpoint.UNKNOWN;
        }
        return Endpoint.UNKNOWN;
    }
}
