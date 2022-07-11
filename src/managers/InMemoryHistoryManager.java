package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList<Task> historyTasksList = new CustomLinkedList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return this.historyTasksList.getTasks();

    }

    @Override
    public <T extends Task> void add(T task) {
        historyTasksList.add(task);
    }

    @Override
    public void deleteTaskFromHistory(UUID id) {
        historyTasksList.removeNode(id);
    }

    public class CustomLinkedList<T extends Task> {
        public Node<T> head;
        public Node<T> last;

        private HashMap<UUID, Node<T>> nodesMap = new HashMap<>();

        private void linkLast(T task) {

            Node<T> lastNode = last;

            Node<T> newNode = new Node<>(last, task, null);
            last = newNode;
            if (lastNode == null) {
                head = newNode;
            } else {
                lastNode.next = newNode;
            }
            nodesMap.put(task.getId(), newNode);
        }

        ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();

            nodesMap.forEach((key, value) -> tasks.add(value.item));

            return tasks;
        }

        private void add(T task) {
            if (task != null) {
                removeNode(task.getId());
                linkLast(task);
            }
        }

        public void removeNode(UUID taskID) {
            Node<T> nodeToDelete = nodesMap.remove(taskID);

            if (nodeToDelete != null) {
                Node<T> previousNode = nodesMap.get(nodeToDelete.prev);
                Node<T> NextNode = nodesMap.get(nodeToDelete.next);

                if (NextNode != null) {
                    NextNode.prev = previousNode;
                }

                if (previousNode != null) {
                    previousNode.next = NextNode;
                }
            }


        }

        private class Node<E extends Task> {
            private Node<E> next;
            private Node<E> prev;
            private E item;

            public Node(Node<E> prev, E element, Node<E> next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }
        }

    }


}


