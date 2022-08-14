package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author A.Gabov
 */
public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> nodes = new HashMap<>();
    private Node head;
    private Node last;

    private void linkLast(Task task) {

        Node lastNode = last;
        Node newNode = new Node(last, task, null);
        last = newNode;
        if (lastNode == null) {
            head = newNode;
        } else {
            lastNode.next = newNode;
        }
        nodes.put(task.getId(), newNode);
    }

    public void removeNode(Integer taskId) {
        Node removedNode = nodes.remove(taskId);

        if (removedNode != null) {
            Node previousNode = removedNode.prev != null ? removedNode.prev : null;
            Node nextNode = removedNode.next != null ? removedNode.next : null;

            if (head == null && last == null) {
                return;
            }

            if (removedNode.equals(this.last) && removedNode.equals(this.head)) {
                last.next = null;
                last.prev = null;
                head = null;
                last = null;
                return;
            }

            if (removedNode.equals(this.last)) {
                last = removedNode.prev;
                last.next = null;
                return;
            }
            if (removedNode.equals(this.head)) {
                head = removedNode.next;
                head.prev = null;
                return;
            }

            if (previousNode != null) {
                previousNode.next = nextNode;
            } else {
                previousNode.next = null;
            }
            if (nextNode != null) {
                nextNode.prev = previousNode;
            }

        }
    }

    public void printHistoryLinks() {
        if (!nodes.values().isEmpty()) {
            nodes.values().forEach(System.out::println);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        nodes.forEach((key, value) -> tasks.add(value.item));

        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (nodes.containsKey(task.getId())) {
                removeNode(task.getId());
            }
            linkLast(task);
        }
    }

    @Override
    public void remove(int taskId) {
        removeNode(taskId);
    }

    private static class Node {
        private Node next;
        private Node prev;
        private Task item;

        public Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            String next = this.next == null ? "null" : this.next.item.getId().toString();
            String prev = this.prev == null ? "null" : this.prev.item.getId().toString();
            return "Node{" +
                    "next=" + next +
                    ", prev=" + prev +
                    ", item=" + item.getId().toString() +
                    '}';
        }
    }

}


