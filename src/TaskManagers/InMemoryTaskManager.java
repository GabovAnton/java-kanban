package TaskManagers;

import HistoryManager.HistoryManager;
import Tasks.EpicTask;
import Tasks.SubTask;
import Tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class InMemoryTaskManager implements TaskManager, PublicInterface {

    private HashMap<UUID, Task> tasks = new HashMap<>(); // тут вообще все таски хранятся
    private HistoryManager historyManager = Managers.getDefaultHistory();

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
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(UUID uid) {

        if (uid != null) {
            if (tasks.containsKey(uid)) {
                return tasks.get(uid);
            } else {
                throw new IllegalArgumentException("Provided element 'task UUID' " + uid + "  not found");
            }
        } else {
            throw new NullPointerException("task UUID cannot be null");

        }
    }

    @Override
    public Task getStandaloneTask(UUID uid) {
        if (!(tasks.get(uid) instanceof  SubTask) && !(tasks.get(uid) instanceof  EpicTask)) {
            updateHistory(tasks.get(uid));
            return getTask(uid);
        } else {
            throw new IllegalArgumentException("Provided element UID is not a StandaloneTask " + uid.toString() );
        }
    }

    @Override
    public SubTask getSubtask(UUID uid) {
        if (tasks.get(uid) instanceof  SubTask) {
            updateHistory(tasks.get(uid));
            return  (SubTask) getTask(uid);
        }
        else {
            throw new IllegalArgumentException("Provided element UID is not a SubTask " + uid.toString() );

        }
    }

    @Override
    public EpicTask getEpic(UUID uid) {
        if (tasks.get(uid) instanceof  EpicTask) {
            updateHistory(tasks.get(uid));
            return (EpicTask) getTask(uid);
        }
        else {
            throw new IllegalArgumentException("Provided element UID is not an Epic " + uid.toString() );

        }
    }

    @Override
    public UUID createEpicTask(EpicTask task) {

        if (task != null) {
            task.setId();
            if (task.getSubTasks().isEmpty()) {
                task.setStatus("NEW");
                tasks.put(task.getId(), task);
                System.out.println(tasks.size());

            } else {
                task.getSubTasks().forEach(subTaskId ->
                        ((SubTask) tasks.get(subTaskId)).setEpicId(task.getId()));
                tasks.put(task.getId(), task);
                updateEpicStatus(task);
            }
            return task.getId();
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public UUID createSubTask(SubTask task) {
        //we should get parent ID to be able to add Tasks.SubTask
        if (task.getEpicId() != null) {
            if (tasks.containsKey(task.getEpicId())) {
                //Эпик есть в общей коллекции
                ((EpicTask) tasks.get(task.getEpicId())).addSubTasks(task.getId());
                tasks.put(task.getId(), task);
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
    public UUID createTask(Task task) {
        //We can accept any task with any subTask, Also, we handle all UUID
        if (task != null) {
            task.setId();
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
            if (tasks.containsKey(task.getId())) {
                EpicTask oldTask = (EpicTask) tasks.get(task.getId());
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
                throw new IllegalArgumentException("Provided element 'task UUID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            if (tasks.containsKey(subTask.getEpicId())) {
                if (tasks.containsKey(subTask.getId())) {
                    SubTask oldTask = (SubTask) tasks.get(subTask.getId());
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
                throw new IllegalArgumentException("Provided element 'task UUID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public boolean deleteTask(UUID id) {
        if (id != null) {
            if (tasks.containsKey(id)) {
                tasks.remove(id);
                return true;
            } else {
                throw new IllegalArgumentException("Provided element 'UUID' " + id.toString() + "  not found");
            }
        } else {
            throw new NullPointerException("UUID object cannot be null");

        }
    }

    @Override
    public void printAllTasks() {
        getTaskList().forEach(task -> {
            if (task instanceof EpicTask) {
                System.out.println(task);
                ((EpicTask) task).getSubTasks().forEach(subTaskId ->
                        System.out.println(getTask(subTaskId).toString()));
            } else if (!(task instanceof SubTask) && !(task instanceof EpicTask)) {
                System.out.println(task);
            }
        });
    }

    @Override
    public ArrayList<SubTask> getAllSubTasksByEpic(EpicTask epic) {

        if (epic != null) {
            ArrayList<SubTask> subtasks = new ArrayList<>();
            epic.getSubTasks().forEach(id -> {
                subtasks.add((SubTask) tasks.get(id));
            });
            return subtasks;
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }

    }

    private <T extends Task> void updateHistory(T task) {
        if (historyManager.getHistory().size() > 9) {
            historyManager.getHistory().remove(0);
        }
        historyManager.getHistory().add(task);
    }


}
