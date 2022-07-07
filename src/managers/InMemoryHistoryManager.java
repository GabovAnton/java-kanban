package managers;

import tasks.Task;

import java.util.*;

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
    public  void deleteTaskFromHistory(UUID id) {
        historyTasksList.removeNode(historyTasksList.getNodeById(id));
    }
    public  class CustomLinkedList<T extends Task> {
        public Node<T> head;
        public Node<T> last;
       //private UUID id;
        private HashMap<UUID, Node<T>> nodesMap = new HashMap<>();

        public Node<T> getNodeById(UUID id) {
            return nodesMap.get(id);
        }

        public HashMap<UUID, Node<T>> getNodesMap() {
            return nodesMap;
        }

        private void linkLast(T task) {

            Node<T> l = last;

            Node<T> newNode = new Node<>(last, task, null);
            last = newNode;
            if (l == null) {
                head = newNode;
            } else
                l.next = newNode;
            nodesMap.put(task.getId(), newNode);
        }

        ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();

            nodesMap.forEach((key, value) -> tasks.add(value.item));

            return tasks;
        }

        private void add(T task) {
            Optional<Map.Entry<UUID, CustomLinkedList<Task>.Node<Task>>> taskNode = historyTasksList.getNodesMap()
                    .entrySet().stream().filter(x -> x.getValue().item.equals(task)).findAny();

            taskNode.ifPresent(uuidNodeEntry -> historyTasksList.removeNode(uuidNodeEntry.getValue()));
            linkLast(task);

        }

        public void removeNode(Node<Task> node) {
            UUID nodeID = getNodeId(node);
            UUID previousNode;
            UUID nextNode;
            if (node.prev != null) {
                previousNode = getNodeId(node.prev);
            } else {
                previousNode = null;
            }
            if (node.next != null) {
                nextNode = getNodeId(node.next);
            } else {
                nextNode = null;
            }

            nodesMap.remove(nodeID);
            if (previousNode != null && nextNode != null) {
                rebuildNodesAfterRemoving(previousNode, nextNode);
            }
        }

        public UUID getNodeId(Node<Task> node) {
            Optional<Map.Entry<UUID, CustomLinkedList<Task>.Node<Task>>> taskNode = historyTasksList.getNodesMap()
                    .entrySet().stream().filter(x -> x.getValue().equals(node)).findAny();

            return taskNode.map(Map.Entry::getKey).orElse(null);
        }

        private void rebuildNodesAfterRemoving(UUID previous, UUID next) {
            if (previous != null) {
                nodesMap.get(previous).setNext(nodesMap.get(next));
            } else {
                nodesMap.get(next).setPrev(null);
            }
            if (next != null) {
                nodesMap.get(next).setPrev(nodesMap.get(previous));
            } else {
                nodesMap.get(previous).setNext(null);
            }

        }

        public class Node<E extends Task> {
            Node<E> next;
            Node<E> prev;
            E item;

            public Node(Node<E> prev, E element, Node<E> next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }


            public void setNext(Node<E> next) {
                this.next = next;
            }

            public void setPrev(Node<E> prev) {
                this.prev = prev;
            }

        }

    }


}


