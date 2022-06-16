package TaskManagers;

import Tasks.EpicTask;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public interface TaskManager {

    void updateEpicStatus(EpicTask task);

    ArrayList<Task> getTaskList();

    void deleteAllTasks();

    Task getTask(UUID uid);

    UUID createEpicTask(EpicTask task);

    UUID createSubTask(SubTask task);

    UUID createTask(Task task);

    void updateEpicTask(EpicTask task);

    void updateSubTask(SubTask task);

    void updateTask(Task task);

    boolean deleteTask(UUID id);
    void printAllTasks();
    ArrayList<SubTask> getAllSubTasksByEpic(EpicTask task);
    public Task getStandaloneTask(UUID uid);
    public SubTask getSubtask(UUID uid);
    public EpicTask getEpic(UUID uid);

}