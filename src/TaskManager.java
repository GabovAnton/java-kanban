import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class TaskManager {
    HashMap<UUID, Task> taskHashMap = new HashMap<>();

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskHashMap.values());
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
    }

    public Task getTask(UUID uid) {
        if (taskHashMap.containsKey(uid)) {
            return taskHashMap.get(uid);
        } else {
            throw new IllegalArgumentException("Provided element 'task UUID' " + uid + "  not found");
        }
    }

    public void createNewEpicTask(EpicTask task) {

        if (task != null) {
            task.setUuid();
            if (task.getSubTasks().isEmpty()) { //New Epic Task with empty subtasks
                task.setStatus(1);
                taskHashMap.put(task.getUuid(), task);
            } else {  //drill down to set uuid for each possible task
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
                    subTask.setUuid();
                    subTask.setEpicTaskUUID(task.getUuid());
                });

            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void createNewSubTask(SubTask task) {
        if (task.getEpicTaskUUID() != null) {//we should get parent ID to be able to add SubTask
            if (taskHashMap.containsKey(task.getEpicTaskUUID())) {
                ((EpicTask) taskHashMap.get(task.getEpicTaskUUID())).addSubTasks(task);
            } else {
                throw new IllegalArgumentException("Provided element 'Epic task UUID' " + task.getEpicTaskUUID() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }

    }

    public void createNewTask(Task task) { //We can accept any task with any subTask, Also, we handle all UUID
        if (task != null) {
            task.setUuid();
            if (!taskHashMap.containsKey(task.getUuid())) {
                taskHashMap.put(task.getUuid(), task);
            } else {
                updateTask(task);
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void updateEpicTask(EpicTask task) {
        if (task != null) {
            if (taskHashMap.containsKey(task.getUuid())) {
                EpicTask oldTask = (EpicTask) taskHashMap.get(task);
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

            } else {
                throw new IllegalArgumentException("Provided element 'task UUID' " + task.getUuid() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void updateSubTask(SubTask task) {
        if (task != null) {
            if (taskHashMap.containsKey(task.getUuid())) {
                SubTask oldTask = (SubTask) taskHashMap.get(task);
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
                oldTask.setEpicTaskUUID(task.getEpicTaskUUID());

            } else {
                throw new IllegalArgumentException("Provided element 'SubTask UUID' " + task.getUuid() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public void updateTask(Task task) {
        if (task != null) {
            if (taskHashMap.containsKey(task.getUuid())) {
                Task oldTask = taskHashMap.get(task);
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
            } else {
                throw new IllegalArgumentException("Provided element 'task UUID' " + task.getUuid() + "  not found");
            }
        } else {
            throw new NullPointerException("Task object cannot be null");
        }
    }

    public boolean deleteTask(UUID uid) {
        if (uid != null) {
            if (taskHashMap.containsKey(uid)) {
                taskHashMap.remove(uid);
                return true;
            } else {
                throw new IllegalArgumentException("Provided element 'UUID' " + uid.toString() + "  not found");
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