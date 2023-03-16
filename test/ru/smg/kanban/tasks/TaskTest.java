package ru.smg.kanban.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {


    @Test
    void setName() {
        Task task = new Task("Old name", "Description", Status.NEW);
        task.setName("New name");
        assertEquals("New name", task.getName(), "Имя не установилось.");
    }

    @Test
    void setDescription() {
        Task task = new Task("Task", "Description", Status.NEW);
        task.setDescription("New description");
        assertEquals("New description", task.getDescription(), "Описание не установилось.");
    }

    @Test
    void setStatus() {
        Task task = new Task("Task", "Description", Status.NEW);
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus(), "Статус не установился.");
    }
}