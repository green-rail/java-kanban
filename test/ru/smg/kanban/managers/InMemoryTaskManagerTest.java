package ru.smg.kanban.managers;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void makeManager() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

}