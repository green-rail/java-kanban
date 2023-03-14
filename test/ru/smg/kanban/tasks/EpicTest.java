package ru.smg.kanban.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;

    @BeforeEach
    public void makeEpic() {
        epic = new Epic("My epic", "Epic description", new ArrayList<>());
    }


    @Test
    void epicWithEmptySubtasksStatusShouldBeNew() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicWithAllNewSubtasksStatusShouldBeNew() {
        epic.getSubtasks().add(new Subtask("Subtask 1", "Description", Status.NEW, epic));
        epic.getSubtasks().add(new Subtask("Subtask 2", "Description", Status.NEW, epic));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicWithAllDoneSubtasksShouldBeDone() {
        epic.getSubtasks().add(new Subtask("Subtask 1", "Description", Status.DONE, epic));
        epic.getSubtasks().add(new Subtask("Subtask 2", "Description", Status.DONE, epic));
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void epicWithNewAndDoneSubtasksShouldBeInProgress() {
        epic.getSubtasks().add(new Subtask("Subtask 1", "Description", Status.NEW, epic));
        epic.getSubtasks().add(new Subtask("Subtask 2", "Description", Status.DONE, epic));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicWithAllInProgressSubtasksShouldBeInProgress() {
        epic.getSubtasks().add(new Subtask("Subtask 1", "Description", Status.IN_PROGRESS, epic));
        epic.getSubtasks().add(new Subtask("Subtask 2", "Description", Status.IN_PROGRESS, epic));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void toStringTest() {
        epic.getSubtasks().add(new Subtask("Subtask 1", "Description", Status.NEW, epic));
        String value =  "[0 epic] My epic(Epic description) [NEW]\n    [0 sub] Subtask 1(Description) [NEW]\n";
        assertEquals(value, epic.toString());
    }
}