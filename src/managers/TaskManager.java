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

    Integer setTaskID();

    void deleteAllTasks();

    Task getTask(Integer uid);

    Integer createEpicTask(EpicTask task);

    Integer createSubTask(SubTask task);

    Integer createTask(Task task);

    void updateEpicTask(EpicTask task);

    void updateSubTask(SubTask task);

    void updateTask(Task task);

    boolean deleteTask(Integer id);

    void printAllTasks();

    List<SubTask> getAllSubTasksByEpic(EpicTask task);

    Task getStandaloneTask(Integer id);

    SubTask getSubtask(Integer id);

    EpicTask getEpic(Integer id);

}