package ru.smg.kanban.managers;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    void makeManager() {
        historyManager = new InMemoryHistoryManager();
    }

}