package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
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

    boolean updateEpicTask(EpicTask task);

    boolean updateSubTask(SubTask subTask);

    boolean updateTask(Task task);

    boolean deleteTask(Integer id);

    boolean deleteAllTasks();

    boolean deleteAllSubTasks();

    boolean deleteAllEpicTasks();

    Task getStandaloneTask(Integer id);

    SubTask getSubtask(Integer id);

    EpicTask getEpic(Integer id);

    boolean deleteEpic(Integer id);

    boolean deleteSubTask(Integer id);

    void printAllTasks();

    List<SubTask> getAllSubTasksByEpic(EpicTask epic);

    List<SubTask> getEpicSubTasks(Integer id) ;

    void setInitialId(int maxId);

    void fillTaskTimeSlots(LocalDateTime start, Period beforeStart, Period afterEnd);

    TreeSet getPrioritizedTasks();


    Boolean isTaskIntersectsExistingRange(Optional<LocalDateTime> start, Optional<LocalDateTime> end);


    Integer getCurrentMaxTaskId();
}