package managers;

import tasks.Task;

import java.util.LinkedList;

/**
 * @author A.Gabov
 */
public interface HistoryManager {

    LinkedList<Task> getHistory();

    <T extends Task> void updateHistory(T task);
}
