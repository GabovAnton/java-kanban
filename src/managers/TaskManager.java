package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

/**
 * @author A.Gabov
 */
public interface TaskManager {

    void updateEpicStatus(EpicTask task);

    List<Task> getTaskList();

    HistoryManager getHistoryManager();

    Integer createEpicTask(EpicTask task);

    Integer createSubTask(SubTask task);

    Integer createTask(Task task);

    void updateEpicTask(EpicTask task);

    void updateSubTask(SubTask subTask);

    void updateTask(Task task);

    boolean deleteTask(Integer id);

    Task getStandaloneTask(Integer id);

    SubTask getSubtask(Integer id);

    EpicTask getEpic(Integer id);

    void printAllTasks();

    List<SubTask> getAllSubTasksByEpic(EpicTask epic);
}