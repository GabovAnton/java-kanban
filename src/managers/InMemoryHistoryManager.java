package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void deleteTaskFromHistory(Integer id) {
        historyTasksList.removeNode(id);
    }

    @Override
    public void printHistoryLinks() {
        historyTasksList.printHistoryLinks();
    }

    public class CustomLinkedList<T extends Task> {
        public Node<T> head;
        public Node<T> last;

        private HashMap<Integer, Node<T>> nodesMap = new HashMap<>();

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

        public void printHistoryLinks() {
            nodesMap.values().forEach(x -> {
                if (x.item != null) {
                    System.out.println("                Current: " + x.item.getId() + "  ");

                    if (x.prev != null) {
                        if (x.prev.item != null) {
                            System.out.print(" Previous: " + x.prev.item.getId());
                        } else {
                            System.out.print(" Previous.item NULL ");
                        }
                    } else {
                        System.out.print(" Previous: NULL");
                    }
                    if (x.next != null) {
                        if (x.next.item != null) {
                            System.out.print("                Next: " + x.next.item.getId());
                        } else {
                            System.out.print("                Next.item NULL ");
                        }
                    } else {
                        System.out.print("                Next: NULL");
                    }

                } else {
                    System.out.println("        Current: NULL");
                }
                System.out.println();

            });
        }

        public void removeNode(Integer taskID) {
            Node<T> nodeToDelete = nodesMap.remove(taskID);

            if (nodeToDelete != null) {

                Node<T> previousNode = nodeToDelete.prev != null ? nodeToDelete.prev : null;
                Node<T> nextNode = nodeToDelete.next != null ? nodeToDelete.next : null;

                if (head == null && last == null) {
                    return;
                }
                if (nodeToDelete.equals(this.last)) {
                    last = nodeToDelete.prev;
                    last.next = null;
                    return;
                }
                if (nodeToDelete.equals(this.head)) {
                    head = nodeToDelete.next;
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


