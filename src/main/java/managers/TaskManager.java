package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author A.Gabov
 */
public interface TaskManager {

    void updateEpicStatus(EpicTask task);

    List<Task> getTasks();

    List<SubTask> getSubtasks();

    List<EpicTask> getEpics();

    HistoryManager getHistoryManager();

    Integer createEpicTask(EpicTask task);

    Integer createSubTask(SubTask task);

    Integer createTask(Task task);

    void updateEpicTask(EpicTask task);

    void updateSubTask(SubTask subTask);

    void updateTask(Task task);

    boolean deleteTask(Integer id);

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpicTasks();

    Task getStandaloneTask(Integer id);

    SubTask getSubtask(Integer id);

    EpicTask getEpic(Integer id);

    boolean deleteEpic(Integer id);

    boolean deleteSubTask(Integer id);

    void printAllTasks();

    List<SubTask> getAllSubTasksByEpic(EpicTask epic);

    void  setInitialId(int maxId);

    void fillTaskTimeSlots();

    TreeSet getPrioritizedTasks();


    Boolean isTaskIntersectsExistingRange(LocalDateTime startTime, LocalDateTime endTime);

    Map<String, Boolean> getSchedule();
}