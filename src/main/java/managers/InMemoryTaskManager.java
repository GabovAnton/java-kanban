package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author A.Gabov
 */
public class InMemoryTaskManager implements TaskManager {
    private static Integer taskId = 0;
    public final HistoryManager historyManager = Managers.getDefaultHistory();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public Map<Map<LocalDateTime, LocalDateTime>, Boolean> getSchedule() {
        return schedule;
    }

    private Map<Map<LocalDateTime, LocalDateTime>, Boolean> schedule =
            new LinkedHashMap(35064, 0.75f, false);

    private TreeSet<Task> sortedTask = new TreeSet(Comparator.comparing(Task::getStartTime));

    private Integer setTaskId() {
        return ++InMemoryTaskManager.taskId;
    }

    public TreeSet getPrioritizedTasks() {
        return this.sortedTask;
    }

    private void addTaskToSortedTreeSet(Task task) {
        sortedTask.add(task);
    }

    private void removeTaskFromSortedTreeSet(Task task) {
        sortedTask.remove(task);
    }

    public void fillTaskTimeSlots() {
        LocalDateTime startDateTime = LocalDateTime.now().withMinute(0);
        LocalDateTime endDateTime = startDateTime.plusYears(1);

        while (startDateTime.isBefore(endDateTime) || startDateTime.isEqual(endDateTime)) {


            schedule.put(Map.of(startDateTime, startDateTime.plusMinutes(15)), false);
            startDateTime = startDateTime.plusMinutes(15);
        }
    }

    private void updateSchedule(LocalDateTime start, LocalDateTime end, boolean flag) {

        LocalDateTime searchOldStart = getSearchEntry(start);
        LocalDateTime searchOldEnd = getSearchEntry(end);

        while (searchOldStart.isBefore(searchOldEnd) || searchOldStart.isEqual(searchOldEnd)) {

            LocalDateTime finalSearchOldStart = searchOldStart;
            LocalDateTime finalSearchOldStart1 = searchOldStart;

          Boolean test =  schedule.entrySet().stream().filter(x -> x.equals(Map.of(finalSearchOldStart, finalSearchOldStart1.plusMinutes(15))))
                    .findFirst().isPresent();

            schedule.entrySet().stream().filter(x -> x.equals(Map.of(finalSearchOldStart, finalSearchOldStart1.plusMinutes(15))))
                    .findFirst().ifPresent(y -> y.setValue(flag));
            searchOldStart = searchOldStart.plusMinutes(15);
        }


    }

    public Boolean isTaskOverlapping(LocalDateTime start, LocalDateTime end) {
        //Формируем первый интервал вхождения
        LocalDateTime firstEntry = getSearchEntry(start);
        LocalDateTime lastEntry = getSearchEntry(end);

        //Ищем первое вхождение и далее с интервалом 15 минут, пока не найдем вхождение от начала до конца задачи
        AtomicReference<Boolean> isExist = new AtomicReference<>(false);
        while (firstEntry.isBefore(lastEntry) || firstEntry.isEqual(lastEntry)) {
            LocalDateTime finalFirstEntry = firstEntry;
            schedule.entrySet()
                    .stream().filter(x -> x.getKey().equals(Map.of(finalFirstEntry, finalFirstEntry.plusMinutes(15))))
                    .findFirst().ifPresent(y -> isExist.set(y.getValue()));
            if (isExist.get()) {
                return true;
            } else {
                firstEntry = firstEntry.plusMinutes(15);
            }
        }
        return isExist.get();
    }

    private LocalDateTime getSearchEntry(LocalDateTime date) {
        LocalDateTime firstEntry = null;

        if (date.getMinute() <= 15) {
            //firstEntry = start.minus(start.getMinute(),ChronoUnit.MINUTES);
            firstEntry = date.withMinute(0);
        } else if (date.getMinute() >= 15 && date.getMinute() < 30) {
            firstEntry = date.withMinute(15);
        } else if (date.getMinute() >= 30 && date.getMinute() < 45) {
            firstEntry = date.withMinute(30);
        } else if (date.getMinute() >= 45 && date.getMinute() < 60) {
            firstEntry = date.withMinute(45);
        }
        return firstEntry;
    }

    public final void setInitialId(int id) {
        taskId = id;
    }

    @Override
    public void updateEpicStatus(EpicTask task) {
        boolean isAllSubTaskIsNewTask = task.getSubTasks().stream().allMatch(subTask ->
                subTasks.get(subTask).getStatus().equals("NEW"));
        boolean isAllSubTaskSsDoneTask = task.getSubTasks().stream().allMatch(subTask ->
                subTasks.get(subTask).getStatus().equals("DONE"));

        if (isAllSubTaskIsNewTask) {
            task.setStatus("NEW");
        } else if (isAllSubTaskSsDoneTask) {
            task.setStatus("DONE");
        } else {
            task.setStatus("IN_PROGRESS");
        }

    }

    private void updateEpicDurationEndStart(EpicTask task) {
        Integer duration = task.getSubTasks().stream().filter(id -> task.getId().equals(id)).mapToInt(subTask ->
                subTasks.get(subTask).getDuration()).sum();
        task.setDuration(duration);


        task.getSubTasks().stream()
                .filter(id -> task.getId().equals(id))
                .map(subTask -> subTasks.get(subTask).getStartTime())
                .min(LocalDateTime::compareTo).ifPresent(x -> {
                    task.setStartTime(x);
                    task.setEndTime(x.plusMinutes(duration));
                });
    }

    @Override
    public List<Task> getTasks() {
        return (List<Task>) getTaskCollection(tasks);
    }

    @Override
    public List<SubTask> getSubtasks() {
        return (List<SubTask>) getTaskCollection(subTasks);
    }

    @Override
    public List<EpicTask> getEpics() {
        return (List<EpicTask>) getTaskCollection(epicTasks);
    }

    private <T extends HashMap<Integer, ? extends Task>> ArrayList<? extends Task> getTaskCollection(T collection) {
        return new ArrayList<>(collection.values());
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void deleteAllTasks() {
        tasks.forEach((key, value) -> historyManager.remove(key));
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {

        subTasks.values().stream().map(SubTask::getEpicId).distinct().forEach(epicId ->
        {
            EpicTask epic = epicTasks.get(epicId);
            epic.removeAllSubtasks();
            updateEpicDurationEndStart(epic);
            updateEpicStatus(epic);
        });

        subTasks.forEach((key, value) -> {
            historyManager.remove(key);
            removeTaskFromSortedTreeSet(value);
        });
        subTasks.clear();


    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.forEach((key, value) -> {
            historyManager.remove(key);
            value.getSubTasks().forEach(subTask -> {
                removeTaskFromSortedTreeSet(subTasks.get(subTask));
                subTasks.remove(subTask);
                historyManager.remove(subTask);
            });
        });

        epicTasks.clear();
    }

    @Override
    public Task getStandaloneTask(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubtask(Integer id) {

        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public EpicTask getEpic(Integer id) {
        if (epicTasks.containsKey(id)) {
            historyManager.add(epicTasks.get(id));
            return epicTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Integer createEpicTask(EpicTask task) {

        if (task != null) {
            if (task.getId() == null) {
                task.setId(setTaskId());
            }
            if (task.getSubTasks().isEmpty()) {
                task.setStatus(TaskStatus.NEW.name());
                epicTasks.put(task.getId(), task);
            } else {
                task.getSubTasks().forEach(subTaskId ->
                        ((SubTask) tasks.get(subTaskId)).setEpicId(task.getId()));
                epicTasks.put(task.getId(), task);
                updateEpicStatus(task);
                updateEpicDurationEndStart(task);
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
                    task.setId(setTaskId());

                }
                (epicTasks.get(task.getEpicId())).addSubTasks(task.getId());
                subTasks.put(task.getId(), task);
                updateEpicDurationEndStart(epicTasks.get(task.getEpicId()));
                updateEpicStatus(epicTasks.get(task.getEpicId()));
                updateSchedule(task.getStartTime(), task.getEndTime(), true);

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
            if (task.getId() == null) {
                task.setId(setTaskId());
            }
            if (!tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
                addTaskToSortedTreeSet(task);
                updateSchedule(task.getStartTime(), task.getEndTime(), true);
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

                if (oldTask.getSubTasks().size() != 0) {
                    oldTask.getSubTasks()
                            .forEach(x ->
                                    updateSchedule(subTasks.get(x).getStartTime(), subTasks.get(x).getEndTime(),
                                            false));
                }
                if (task.getSubTasks() == null) {
                    oldTask.getSubTasks().clear();
                } else if (oldTask.getSubTasks() == null && task.getSubTasks() != null) {
                    oldTask.getSubTasks().addAll(task.getSubTasks());
                    task.getSubTasks()
                            .forEach(x ->
                                    updateSchedule(subTasks.get(x).getStartTime(), subTasks.get(x).getEndTime(),
                                            true));
                }
                updateEpicStatus(task);
                updateEpicDurationEndStart(task);


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
                    removeTaskFromSortedTreeSet(oldTask);
                    addTaskToSortedTreeSet(subTask);
                    updateSchedule(oldTask.getStartTime(), oldTask.getEndTime(),
                            false);
                    updateSchedule(subTask.getStartTime(), subTask.getEndTime(),
                            true);
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
                removeTaskFromSortedTreeSet(oldTask);
                addTaskToSortedTreeSet(task);
                updateSchedule(oldTask.getStartTime(), oldTask.getEndTime(), false);
                updateSchedule(task.getStartTime(), task.getEndTime(), true);
            } else {
                throw new IllegalArgumentException("Provided element 'task ID' " + task.getId() + "  not found");
            }
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public boolean deleteTask(Integer id) {
        boolean result;

        if (id != null) {
            if (tasks.containsKey(id)) {
                removeTaskFromSortedTreeSet(tasks.get(id));
                updateSchedule(tasks.get(id).getStartTime(), tasks.get(id).getEndTime(), false);

                tasks.remove(id);
                historyManager.remove(id);
                result = true;
            } else {
                throw new IllegalArgumentException("Provided element 'id' " + id + "  not found");
            }

        } else {
            throw new NullPointerException("id object cannot be null");
        }
        return result;
    }

    @Override
    public boolean deleteEpic(Integer id) {
        boolean result = false;

        if (id != null) {
            if (epicTasks.containsKey(id)) {
                EpicTask epicTaskToDelete = epicTasks.get(id);
                epicTaskToDelete.getSubTasks().forEach(k -> {
                    updateSchedule(subTasks.get(id).getStartTime(), subTasks.get(id).getEndTime(), false);
                    removeTaskFromSortedTreeSet(subTasks.get(id));
                    subTasks.remove(k);
                    historyManager.remove(k);
                });

                epicTasks.remove(id);
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
    public boolean deleteSubTask(Integer id) {
        boolean result = false;

        if (id != null) {
            if (subTasks.containsKey(id)) {
                SubTask subTaskToDelete = subTasks.get(id);
                int epicID = subTaskToDelete.getEpicId();
                removeTaskFromSortedTreeSet(subTasks.get(id));
                updateSchedule(subTasks.get(id).getStartTime(), subTasks.get(id).getEndTime(), false);

                subTasks.remove(id);
                epicTasks.get(epicID).getSubTasks().remove(subTaskToDelete.getId());
                updateEpicDurationEndStart(epicTasks.get(epicID));
                updateEpicStatus(epicTasks.get(epicID));
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
        this.getTasks().forEach(task -> {
            if (task instanceof EpicTask) {
                System.out.println(task);
                ((EpicTask) task).getSubTasks().forEach(subTaskId ->
                        System.out.println(getSubtask(subTaskId).toString()));
            } else if (!(task instanceof SubTask)) {
                System.out.println(task);
            }
            historyManager.add(task);
        });
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(EpicTask epic) {

        if (epic != null) {
            historyManager.add(epic);

            List<SubTask> subTasks = new ArrayList<>();
            epic.getSubTasks().forEach(id -> {
                subTasks.add(this.subTasks.get(id));
                historyManager.add(this.subTasks.get(id));
            });

            return subTasks;
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }

    }

}
