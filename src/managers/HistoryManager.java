package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public interface HistoryManager {

    ArrayList<Task> getHistory();
    <T extends Task> void add(T task);

    void deleteTaskFromHistory(UUID taskid1);
}
