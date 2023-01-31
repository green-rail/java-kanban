package ru.smg.kanban.managers;

import ru.smg.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, HistoryNode> map = new HashMap<>();
    private HistoryNode head;
    private HistoryNode tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        removeNode(map.get(task.getId()));
        map.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(map.remove(id));
    }

    private void removeNode(HistoryNode node) {
        if (node == null) {
            return;
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else if (node.equals(head)) {
            head = node.getNext();
        }
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else if (node.equals(tail)) {
            tail = node.getPrev();
        }
    }

    private HistoryNode linkLast(Task task) {
        var node = new HistoryNode(task);
        if (head == null) {
            head = node;
            tail = node;
            return node;
        }
        node.setPrev(tail);
        tail.setNext(node);
        tail = node;
        return node;
    }

    private List<Task> getTasks() {
        var list = new ArrayList<Task>(map.size());
        var node = head;
        while (node != null) {
            list.add(node.getValue());
            node = node.getNext();
        }
        return list;
    }

    private class HistoryNode {
        private HistoryNode prev;
        private HistoryNode next;
        private final Task value;

        public HistoryNode(Task task) {
            this.value = task;
        }

        public HistoryNode getNext() {
            return next;
        }

        public HistoryNode getPrev() {
            return prev;
        }

        public Task getValue() {
            return value;
        }

        public void setPrev(HistoryNode prev) {
            this.prev = prev;
        }

        public void setNext(HistoryNode nextNode) {
            this.next = nextNode;
        }
    }
}
