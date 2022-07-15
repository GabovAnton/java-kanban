package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author A.Gabov
 */
public class InMemoryTaskManager implements TaskManager {
    public static HistoryManager historyManager = Managers.getDefaultHistory();
    private static Integer taskID = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private Integer setTaskID() {
        return ++InMemoryTaskManager.taskID;
    }

    @Override
    public void updateEpicStatus(EpicTask task) {
        boolean isAllSubTaskIsNewTask = task.getSubTasks().stream().allMatch(subTask ->
                tasks.get(subTask).getStatus().equals("NEW"));
        boolean isAllSubTaskSsDoneTask = task.getSubTasks().stream().allMatch(subTask ->
                tasks.get(subTask).getStatus().equals("DONE"));

        if (isAllSubTaskIsNewTask) {
            task.setStatus("NEW");
        } else if (isAllSubTaskSsDoneTask) {
            task.setStatus("DONE");
        } else {
            task.setStatus("IN_PROGRESS");
        }

    }

    @Override
    public List<Task> getTaskList() {
        List<Task> allTasks = new ArrayList<>();
        addCollectionToHistory(tasks, allTasks);
        addCollectionToHistory(epicTasks, allTasks);
        addCollectionToHistory(subTasks, allTasks);

        return allTasks;
    }

    private <T extends HashMap<Integer, ? extends Task>> void addCollectionToHistory(T collection, List<Task> allTasks) {
        collection.values().forEach(x -> {
            allTasks.add(x);
            historyManager.add(x);
        });
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Task getStandaloneTask(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new IllegalArgumentException("Provided element 'task ID' " + id + "  not found");
        }
    }

    @Override
    public SubTask getSubtask(Integer id) {
        if (id != null) {
            if (subTasks.containsKey(id)) {
                historyManager.add(subTasks.get(id));
                return subTasks.get(id);
            } else {
                throw new IllegalArgumentException("Provided element 'subtask ID' " + id + "  not found");
            }
        } else {
            throw new NullPointerException("task ID cannot be null");
        }

    }

    @Override
    public EpicTask getEpic(Integer id) {
        if (epicTasks.containsKey(id)) {
            historyManager.add(epicTasks.get(id));
            return epicTasks.get(id);
        } else {
            throw new IllegalArgumentException("Provided element UID is not an Epic " + id.toString());

        }
    }

    @Override
    public Integer createEpicTask(EpicTask task) {

        if (task != null) {
            task.setId(setTaskID());
            if (task.getSubTasks().isEmpty()) {
                task.setStatus("NEW");
                epicTasks.put(task.getId(), task);
            } else {
                task.getSubTasks().forEach(subTaskId ->
                        ((SubTask) tasks.get(subTaskId)).setEpicId(task.getId()));
                epicTasks.put(task.getId(), task);
                updateEpicStatus(task);
            }
            return task.getId();
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public Integer createSubTask(SubTask task) {
        if (task.getEpicId() != null) {
            if (epicTasks.containsKey(task.getEpicId())) {
                if (task.getId() == null) {
                    task.setId(setTaskID());
                }
                (epicTasks.get(task.getEpicId())).addSubTasks(task.getId());
                subTasks.put(task.getId(), task);
            } else {
                throw new IllegalArgumentException("Provided element 'Epic task id' " + task.getEpicId() +
                        "  not found");
            }
            return task.getId();
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }

    }

    @Override
    public Integer createTask(Task task) {
        if (task != null) {
            task.setId(setTaskID());
            if (!tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            } else {
                throw new IllegalArgumentException("Provided object 'task' " + task.getId() + "  " +
                        "has been already stored in manager with same id");
            }
            return task.getId();
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public void updateEpicTask(EpicTask task) {
        if (task != null) {
            if (epicTasks.containsKey(task.getId())) {
                EpicTask oldTask = epicTasks.get(task.getId());
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());

                if (task.getSubTasks() == null) {
                    oldTask.getSubTasks().clear();
                } else if (oldTask.getSubTasks() == null && task.getSubTasks() != null) {
                    oldTask.getSubTasks().addAll(task.getSubTasks());
                }
                updateEpicStatus(task);
            } else {
                throw new IllegalArgumentException("Provided element 'task ID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            if (epicTasks.containsKey(subTask.getEpicId())) {
                if (subTasks.containsKey(subTask.getId())) {
                    SubTask oldTask = subTasks.get(subTask.getId());
                    oldTask.setName(subTask.getName());
                    oldTask.setDescription(subTask.getDescription());
                    oldTask.setStatus(subTask.getStatus());
                    oldTask.setEpicId(subTask.getEpicId());
                } else {
                    throw new IllegalArgumentException("Provided element 'Tasks.SubTask id' " + subTask.getId() + "  not found");
                }

            } else {
                throw new IllegalArgumentException("Provided element 'Tasks.SubTask' contains epicID witch doesn't belongs " +
                        "to tasks Array " + subTask.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                Task oldTask = tasks.get(task.getId());
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
            } else {
                throw new IllegalArgumentException("Provided element 'task ID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public boolean deleteTask(Integer id) {
        boolean result = false;

        if (id != null) {
            if (tasks.containsKey(id)) {
                tasks.remove(id);
                historyManager.remove(id);
                result = true;
            } else if (epicTasks.containsKey(id)) {
                EpicTask epicTaskToDelete = epicTasks.get(id);
                epicTaskToDelete.getSubTasks().forEach(k -> {
                    subTasks.remove(k);
                    historyManager.remove(k);
                });

                epicTasks.remove(id);
                historyManager.remove(id);

            } else if (subTasks.containsKey(id)) {
                SubTask subTaskToDelete = subTasks.get(id);
                int epicID = subTaskToDelete.getEpicId();

                subTasks.remove(id);
                epicTasks.get(epicID).getSubTasks().remove(subTaskToDelete.getId());
                historyManager.remove(id);
            } else {
                throw new IllegalArgumentException("Provided element 'id' " + id + "  not found");
            }

        } else {
            throw new NullPointerException("id object cannot be null");
        }
        return result;
    }

    @Override
    public void printAllTasks() {
        getTaskList().forEach(task -> {
            if (task instanceof EpicTask) {
                System.out.println(task);
                ((EpicTask) task).getSubTasks().forEach(subTaskId ->
                        System.out.println(getSubtask(subTaskId).toString()));
            } else if (!(task instanceof SubTask)) {
                System.out.println(task);
            }
        });
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(EpicTask epic) {

        if (epic != null) {
            ArrayList<SubTask> subtasks = new ArrayList<>();
            epic.getSubTasks().forEach(id -> subtasks.add(subTasks.get(id)));
            return subtasks;
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }

    }

}
