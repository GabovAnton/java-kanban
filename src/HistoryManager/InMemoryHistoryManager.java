package HistoryManager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> HistoryTasks = new ArrayList<>();

    @Override
    public void add(Task task) {

    }

    @Override
    public List<Task> getHistory() {
        return this.HistoryTasks;
    }
}
