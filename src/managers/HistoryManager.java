package managers;

import tasks.Task;

import java.util.ArrayList;

/**
 * @author A.Gabov
 */
public interface HistoryManager {

    ArrayList<Task> getHistory();

    <T extends Task> void add(T task);

    void deleteTaskFromHistory(Integer taskid1);

    void printHistoryLinks();
}
