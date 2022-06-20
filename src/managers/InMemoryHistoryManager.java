package managers;

import tasks.Task;

import java.util.LinkedList;

/**
 * @author A.Gabov
 */
public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> historyTasks = new LinkedList<>();

    @Override
    public void add(Task task) {

    }

    @Override
    public LinkedList<Task> getHistory() {
        return this.historyTasks;
    }

    @Override
    public <T extends Task> void updateHistory(T task) {
        if (getHistory().size() > 9) {
            getHistory().removeFirst();
        }
        getHistory().add(task);
    }
}
