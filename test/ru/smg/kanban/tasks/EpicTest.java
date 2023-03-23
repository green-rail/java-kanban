package ru.smg.kanban.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;

    @BeforeEach
    public void makeEpic() {
        epic = new Epic("My epic", "Epic description");
    }

    @Test
    void epicWithEmptySubtasksStatusShouldBeNew() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void epicWithSameSubtasksShouldMatchStatus(Status status) {
        epic.addSubtask(new Subtask("Subtask 1", "Description", status, epic.getId()));
        epic.addSubtask(new Subtask("Subtask 2", "Description", status, epic.getId()));
        assertEquals(status, epic.getStatus());
    }

    @Test
    void epicWithNewAndDoneSubtasksShouldBeInProgress() {
        epic.addSubtask(new Subtask("Subtask 1", "Description", Status.NEW, epic.getId()));
        epic.addSubtask(new Subtask("Subtask 2", "Description", Status.DONE, epic.getId()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
    @Test
    void toStringTest() {
        epic.addSubtask(new Subtask("Subtask 1", "Description", Status.NEW, epic.getId()));
        String value =  "[-1 epic] My epic(Epic description) [NEW]\n    [-1 sub] Subtask 1(Description) [NEW]\n";
        assertEquals(value, epic.toString());
    }

    @Test
    void epicDurationShouldBeSumOfSubtasks() {
        var sub1 = new Subtask(1, "Subtask 1", "Description", Status.NEW, epic.getId());
        sub1.setDuration(Duration.ofMinutes(30));
        var sub2 = new Subtask(2, "Subtask 2", "Description", Status.NEW, epic.getId());
        sub2.setDuration(Duration.ofMinutes(15));
        epic.addSubtask(sub1);
        epic.addSubtask(sub2);
        assertEquals(45, epic.getDuration().toMinutes(), "Длительность не верная.");
    }

    @Test
    void epicStartEndTimeShouldBeFromSubtasks() {
        var sub1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId());
        LocalDateTime startTime = LocalDateTime.now();
        sub1.setStartTime(startTime);
        var sub2 = new Subtask("Subtask 2", "Description", Status.NEW, epic.getId());
        sub2.setStartTime(startTime.plusMinutes(30));
        var sub3 = new Subtask("Subtask 3", "Description", Status.NEW, epic.getId());
        LocalDateTime endTime = startTime.plusMinutes(50);
        sub3.setStartTime(endTime);
        sub3.setDuration(Duration.ofMinutes(50));
        epic.addSubtask(sub1);
        epic.addSubtask(sub2);
        epic.addSubtask(sub3);
        assertEquals(startTime, epic.getStartTime(), "Начальное время неверное.");
        assertEquals(sub3.getEndTime(), epic.getEndTime(), "Конечное время неверное.");
    }

    @Test
    void removeSubtask() {
        var sub1 = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId());
        epic.addSubtask(sub1);
        assertEquals(1, epic.getSubtasks().size(), "Подзадача не добавилась.");
        epic.removeSubtask(sub1);
        assertEquals(0, epic.getSubtasks().size(), "Подзадача не удалилась.");
    }
}