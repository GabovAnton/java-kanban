package HistoryManager;

import Tasks.Task;

import java.util.List;

/**
 * @author A.Gabov
 */
public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

}
