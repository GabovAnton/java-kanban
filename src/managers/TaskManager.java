package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.List;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public interface TaskManager {

    void updateEpicStatus(EpicTask task);

    List<Task> getTaskList();

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

    List<SubTask> getAllSubTasksByEpic(EpicTask task);

    Task getStandaloneTask(UUID id);

    SubTask getSubtask(UUID id);

    EpicTask getEpic(UUID id);

}