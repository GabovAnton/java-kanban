package managers;

import tasks.Task;

import java.util.ArrayList;

/**
 * @author A.Gabov
 */
public interface HistoryManager {

    ArrayList<Task> getHistory();

    void add(Task task);

    void remove(int taskId);

}
