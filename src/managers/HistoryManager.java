package managers;

import tasks.Task;

import java.util.LinkedList;

/**
 * @author A.Gabov
 */
public interface HistoryManager {
    void add(Task task);

    LinkedList<Task> getHistory();

    <T extends Task> void updateHistory(T task);
}
