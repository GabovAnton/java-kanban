import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class TaskManager {

    private HashMap<UUID, Task> tasks = new HashMap<>();

    public static void updateEpicStatus(EpicTask task) {
        boolean isAllSubTaskIsNewTask = task.getSubTasks().stream().allMatch(subTask -> subTask.getStatus() == 1);
        boolean isAllSubTaskSsDoneTask = task.getSubTasks().stream().allMatch(subTask -> subTask.getStatus() == 3);

        if (isAllSubTaskIsNewTask) {
            task.setStatus(1);
        } else if (isAllSubTaskSsDoneTask) {
            task.setStatus(3);
        } else {
            task.setStatus(2);
        }
        task.getSubTasks().forEach(subTask -> {
            subTask.setId();
            subTask.setEpicId(task.getId());
        });
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

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

    public UUID createEpicTask(EpicTask task) {

        if (task != null) {
            task.setId();
            if (task.getSubTasks().isEmpty()) {
                task.setStatus(1);
                tasks.put(task.getId(), task);
                System.out.println(tasks.size());

            } else {
                task.getSubTasks().forEach(subtask -> subtask.setEpicId(task.getId()));
                tasks.put(task.getId(), task);
                updateEpicStatus(task);
            }
            return task.getId();
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public UUID createSubTask(SubTask task) {
        //we should get parent ID to be able to add SubTask
        if (task.getEpicId() != null) {
            if (tasks.containsKey(task.getEpicId())) {
                ((EpicTask) tasks.get(task.getEpicId())).addSubTasks(task);
            } else {
                throw new IllegalArgumentException("Provided element 'Epic task UUID' " + task.getEpicId() + "  not found");
            }
            return task.getId();
        } else {
            throw new NullPointerException("Task object cannot be null");
        }

    }

    public UUID createTask(Task task) {
        //We can accept any task with any subTask, Also, we handle all UUID
        if (task != null) {
            task.setId();
            if (!tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            } else {
                updateTask(task);
            }
            return task.getId();
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void updateEpicTask(EpicTask task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                EpicTask oldTask = (EpicTask) tasks.get(task);
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());

                if (oldTask.getSubTasks() != null && (task.getSubTasks() == null)) {
                    oldTask.getSubTasks().clear();
                    oldTask.getSubTasks().addAll(task.getSubTasks());
                } else if (task.getSubTasks() == null) {
                    oldTask.getSubTasks().clear();
                } else if (oldTask.getSubTasks() == null && task.getSubTasks() != null) {
                    task.getSubTasks().forEach(subTask -> task.addSubTasks(subTask));
                }
                 updateEpicStatus(task);
            } else {
                throw new IllegalArgumentException("Provided element 'task UUID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void updateSubTask(SubTask task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                SubTask oldTask = (SubTask) tasks.get(task);
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
                oldTask.setEpicId(task.getEpicId());
                updateEpicStatus((EpicTask) tasks.get(oldTask.getEpicId()));
            } else {
                throw new IllegalArgumentException("Provided element 'SubTask id' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void updateTask(Task task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                Task oldTask = tasks.get(task);
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
            } else {
                throw new IllegalArgumentException("Provided element 'task UUID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

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

    public List<Task> getAllSubTasksByEpic(EpicTask task) {

        if (task != null) {
            return new ArrayList<>(task.getSubTasks());
        } else {
            throw new NullPointerException("Task object cannot be null");
        }

    }

}