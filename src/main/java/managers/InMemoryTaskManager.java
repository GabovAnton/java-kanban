package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import util.ScheduleDateTimeCell;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

/**
 * @author A.Gabov
 */
public class InMemoryTaskManager implements TaskManager {


    private static Integer taskId = 0;

    private static final Map<ScheduleDateTimeCell, Boolean> schedule =
            new LinkedHashMap(35064, 0.75f, false);

    public final HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    Comparator<Task> sortedTaskOptionalComparator =
            comparing(
                    Task::getStartTime,
                    comparing(opt -> opt.isPresent() ?
                                    opt.orElse(null) : null,
                            nullsLast(naturalOrder())
                    ));

    public Integer getCurrentMaxTaskId() {
        final Integer maxId = Integer.valueOf(taskId);
        return maxId;
    }

    protected boolean restoreTasksCollection(List<Task> tasks) {
        final HashMap<Integer, Task> oldTasksInMemory = this.tasks;
        try {
            this.tasks.clear();
            for (Task task : tasks) {
                this.tasks.put(task.getId(), task);
                this.sortedTask.add(task);
            }
        } catch (Exception ex) {
            this.tasks.putAll(oldTasksInMemory);
        }

        return true;
    }

    protected boolean restoreSubTasksCollection(List<SubTask> subTasks) {
        final HashMap<Integer, SubTask> oldSubTasksInMemory = this.subTasks;
        try {
            this.subTasks.clear();
            for (SubTask subTask : subTasks) {
                this.subTasks.put(subTask.getId(), subTask);
                this.sortedTask.add(subTask);
            }
        } catch (Exception ex) {
            this.subTasks.putAll(oldSubTasksInMemory);
        }

        return true;
    }

    protected boolean restoreEpicTasksCollection(List<EpicTask> epicTasks) {
        final HashMap<Integer, EpicTask> oldEpicTasksInMemory = this.epicTasks;
        try {
            this.epicTasks.clear();
            for (EpicTask epicTask : epicTasks) {
                this.epicTasks.put(epicTask.getId(), epicTask);
            }
        } catch (Exception ex) {
            this.epicTasks.putAll(oldEpicTasksInMemory);
        }

        return true;
    }

    private final TreeSet<Task> sortedTask = new TreeSet(sortedTaskOptionalComparator);

    protected Map<ScheduleDateTimeCell, Boolean> getSchedule() {
        return schedule;
    }

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

    public void fillTaskTimeSlots(LocalDateTime start, Period beforeStart, Period afterStart) {
        schedule.clear();
        LocalDateTime startDateTime = start.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plus(afterStart);
        startDateTime = startDateTime.minus(beforeStart);


        while (startDateTime.isBefore(endDateTime) || startDateTime.isEqual(endDateTime)) {

            schedule.put(new ScheduleDateTimeCell(startDateTime), false);
            startDateTime = startDateTime.plusMinutes(15);
        }
    }

    private void verifyIfTaskWithinSchedulePeriod(LocalDateTime start, LocalDateTime end) {
        LocalDateTime searchStart = ScheduleDateTimeCell.constructDateTime(start);
        LocalDateTime searchEnd = ScheduleDateTimeCell.constructDateTime(end);

        Optional<LocalDateTime> startSchedule = schedule.keySet().stream().map(ScheduleDateTimeCell::getStart)
                .min(LocalDateTime::compareTo);
        Optional<LocalDateTime> endSchedule = schedule.keySet().stream().map(ScheduleDateTimeCell::getEnd)
                .max(LocalDateTime::compareTo);

        if (startSchedule.isPresent()) {
            if (searchStart.isBefore(startSchedule.get()) || searchEnd.isBefore(startSchedule.get())) {
                throw new IllegalArgumentException("Provided date range for Task is is less then start date in Schedule");
            }
        }

        if (endSchedule.isPresent()) {
            if (searchEnd.isAfter(endSchedule.get()) || searchStart.isAfter(endSchedule.get())) {
                throw new IllegalArgumentException("Provided date range for Task is is greater then start date in Schedule");
            }
        }
    }

    private void updateSchedule(Optional<LocalDateTime> start, Optional<LocalDateTime> end, boolean flag) {
        if (start.isEmpty() || end.isEmpty()) {
            return;
        }
        LocalDateTime searchStart = ScheduleDateTimeCell.constructDateTime(start.get());
        LocalDateTime searchEnd = ScheduleDateTimeCell.constructDateTime(end.get());

        verifyIfTaskWithinSchedulePeriod(searchStart, searchEnd);


        while (searchStart.isBefore(searchEnd) || searchStart.isEqual(searchEnd)) {

            schedule.entrySet()
                    .stream()
                    .filter(x -> x.getKey().equals(new ScheduleDateTimeCell(start.get())))
                    .findFirst().ifPresent(y -> y.setValue(flag));
            searchStart = searchStart.plusMinutes(15);
        }


    }

    public Boolean isTaskIntersectsExistingRange(Optional<LocalDateTime> start, Optional<LocalDateTime> end) {
        //Формируем первый интервал вхождения


        if (!start.isEmpty() && !end.isEmpty()) {
            LocalDateTime firstEntry = ScheduleDateTimeCell.constructDateTime(start.get());
            LocalDateTime lastEntry = ScheduleDateTimeCell.constructDateTime(end.get());

            //Ищем первое вхождение и далее с интервалом 15 минут, пока не найдем вхождение от начала до конца задачи
            AtomicReference<Boolean> isExist = new AtomicReference<>(false);
            while (firstEntry.isBefore(lastEntry) || firstEntry.isEqual(lastEntry)) {
                ScheduleDateTimeCell scheduleDateTimeCell = new ScheduleDateTimeCell(firstEntry);

                schedule.entrySet()
                        .stream().filter(x -> x.getKey().equals(scheduleDateTimeCell))
                        .findFirst().ifPresent(y -> isExist.set(y.getValue()));
                if (isExist.get()) {
                    return true;
                } else {
                    firstEntry = firstEntry.plusMinutes(15);
                }
            }
            return isExist.get();


        } else {
            return false;
        }
    }

    public final void setInitialId(int id) {
        taskId = id;
    }

    @Override
    public void updateEpicStatus(EpicTask epicTask) {

        if (epicTasks.containsKey(epicTask.getId())) {
            EpicTask epicToUpdate = epicTasks.get(epicTask.getId());

            boolean isAllSubTaskIsNewTask = epicToUpdate.getSubTasks().stream().allMatch(subTask ->
                    subTasks.get(subTask).getStatus().equals("NEW"));
            boolean isAllSubTaskSsDoneTask = epicToUpdate.getSubTasks().stream().allMatch(subTask ->
                    subTasks.get(subTask).getStatus().equals("DONE"));

            if (isAllSubTaskIsNewTask) {
                epicToUpdate.setStatus("NEW");
            } else if (isAllSubTaskSsDoneTask) {
                epicToUpdate.setStatus("DONE");
            } else {
                epicToUpdate.setStatus("IN_PROGRESS");
            }
        } else {
            throw new IllegalArgumentException("Provided Epic with id: " + epicTask.getId() + " doesn't exist");
        }


    }

    private void updateEpicDurationEndStart(EpicTask epicTask) {

        Optional.of(epicTask.getSubTasks().stream()
                .map(subTaskId -> subTasks.get(subTaskId).getDuration())
                .filter(Optional::isPresent)
                .mapToInt(Optional::get)
                .sum()).ifPresent(x -> epicTask.setDuration(Optional.of(x)));

        epicTask.getSubTasks().stream()
                .map(subTask -> subTasks.get(subTask).getStartTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(LocalDateTime::compareTo)
                .ifPresent(x -> {
                    epicTask.setStartTime(x);
                    epicTask.setEndTime(x.plusMinutes(epicTask.getDuration().orElse(0)));
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
    public boolean deleteAllTasks() {
        tasks.forEach((key, value) -> {
            updateSchedule(value.getStartTime(), value.getEndTime(), false);
            historyManager.remove(key);
        });
        tasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllSubTasks() {

        subTasks.values().stream().map(SubTask::getEpicId).distinct().forEach(epicId ->
        {
            EpicTask epic = epicTasks.get(epicId);
            epic.removeAllSubtasks();
            updateEpicDurationEndStart(epic);
            updateEpicStatus(epic);
        });

        subTasks.forEach((key, value) -> {
            updateSchedule(value.getStartTime(), value.getEndTime(), false);
            historyManager.remove(key);
            removeTaskFromSortedTreeSet(value);
        });
        subTasks.clear();


        return true;
    }

    @Override
    public boolean deleteAllEpicTasks() {
        epicTasks.forEach((key, value) -> {
            historyManager.remove(key);
            value.getSubTasks().forEach(subTaskId -> {
                updateSchedule(subTasks.get(subTaskId).getStartTime(), subTasks.get(subTaskId).getEndTime(), false);
                removeTaskFromSortedTreeSet(subTasks.get(subTaskId));
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            });
        });

        epicTasks.clear();
        return true;
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
    public Integer createEpicTask(EpicTask epicTask) {

        if (epicTask != null) {
            if (epicTask.getId() == null) {
                epicTask.setId(setTaskId());
            }
            if (epicTask.getSubTasks().isEmpty()) {
                epicTask.setStatus(TaskStatus.NEW.name());
                epicTasks.put(epicTask.getId(), epicTask);
            } else {
                epicTask.getSubTasks().forEach(subTaskId ->
                        (subTasks.get(subTaskId)).setEpicId(epicTask.getId()));
                epicTasks.put(epicTask.getId(), epicTask);
                updateEpicStatus(epicTask);
                updateEpicDurationEndStart(epicTask);
            }
            return epicTask.getId();
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

                if ((task.getEndTime().isPresent() || (task.getStartTime().isEmpty() && task.getDuration().isEmpty()))) {
                    if (!isTaskIntersectsExistingRange(task.getStartTime(), task.getEndTime())) {
                        (epicTasks.get(task.getEpicId())).addSubTasks(task.getId());
                        subTasks.put(task.getId(), task);
                        updateEpicDurationEndStart(epicTasks.get(task.getEpicId()));
                        updateEpicStatus(epicTasks.get(task.getEpicId()));
                        updateSchedule(task.getStartTime(), task.getEndTime(), true);
                        addTaskToSortedTreeSet(task);
                    } else {
                        throw new IllegalArgumentException(
                                "Provided date range for subTask is intersects with exiting one");
                    }
                } else {
                    throw new IllegalArgumentException("Duration and Start should be both filled up or both empty");
                }

            } else {
                throw new IllegalArgumentException("Provided element 'Epic task id' " + task.getEpicId() +
                        "  not found");
            }
            return task.getId();
        } else {
            throw new NullPointerException("EpicID in subtask object cannot be null");
        }

    }

    @Override
    public Integer createTask(Task task) {
        if (task != null) {
            if (task.getId() == null) {
                task.setId(setTaskId());
            }

            Optional<LocalDateTime> start = task.getStartTime().isEmpty() ? Optional.empty() : task.getStartTime();
            Optional<LocalDateTime> end = task.getDuration().isEmpty() ? Optional.empty() : task.getEndTime();

            if (!isTaskIntersectsExistingRange(start, end)) {
                if (!tasks.containsKey(task.getId())) {
                    tasks.put(task.getId(), task);
                    addTaskToSortedTreeSet(task);
                    updateSchedule(start, end, true);

                } else {
                    throw new IllegalArgumentException("Provided object 'task' " + task.getId() + "  " +
                            "has been already stored in manager with same id");
                }
            } else {

                throw new IllegalArgumentException("Provided date range for Task id: " +
                        task.getId() + " name(" + task.getName() + ") is intersects with exiting one");
            }
            return task.getId();
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            if (epicTasks.containsKey(epicTask.getId())) {
                EpicTask oldTask = epicTasks.get(epicTask.getId());
                oldTask.setName(epicTask.getName());
                oldTask.setDescription(epicTask.getDescription());
                return true;
            } else {
                throw new IllegalArgumentException("Provided element 'task ID' " + epicTask.getId() + "  not found");
            }
        } else {

            throw new NullPointerException("EpicTask object cannot be null");
        }
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (subTask != null) {
            final SubTask existingTask = new SubTask(subTasks.get(subTask.getId()));
            updateSchedule(existingTask.getStartTime(), existingTask.getEndTime(), false);


            if (!isTaskIntersectsExistingRange(subTask.getStartTime(), subTask.getEndTime())) {

                if (epicTasks.containsKey(subTask.getEpicId())) {
                    createSubTask(subTask);

                    addTaskToSortedTreeSet(subTask);
                    updateSchedule(subTask.getStartTime(), subTask.getEndTime(),
                            true);
                    // historyManager
                    return true;


                } else {
                    throw new IllegalArgumentException("Provided element 'Tasks.SubTask' contains epicID witch doesn't belongs " +
                            "to tasks Array " + subTask.getId() + "  not found");
                }
            } else {
                updateSchedule(existingTask.getStartTime(), existingTask.getEndTime(), true);

                throw new IllegalArgumentException("Provided date range for subTask is intersects with exiting one");
            }

        } else {

            throw new NullPointerException("Tasks.Task object cannot be null");
        }
    }

    @Override
    public boolean updateTask(Task task) {
        final Task existingTask = new Task(tasks.get(task.getId()));
        updateSchedule(existingTask.getStartTime(), existingTask.getEndTime(), false);
        if (!isTaskIntersectsExistingRange(task.getStartTime(), task.getEndTime())) {

            if (tasks.containsKey(task.getId())) {
                Task oldTask = tasks.get(task.getId());
                oldTask.setName(task.getName());
                oldTask.setDescription(task.getDescription());
                oldTask.setStatus(task.getStatus());
                removeTaskFromSortedTreeSet(oldTask);
                addTaskToSortedTreeSet(task);
                updateSchedule(task.getStartTime(), task.getEndTime(), true);
                return true;
            } else {
                throw new IllegalArgumentException("Provided element 'task ID' " + task.getId() + "  not found");
            }
        } else {
            updateSchedule(existingTask.getStartTime(), existingTask.getEndTime(), true);
            throw new IllegalArgumentException("Provided date range for task is intersects with exiting one");

        }
    }

    @Override
    public boolean deleteTask(Integer id) {

        if (id != null) {
            if (tasks.containsKey(id)) {
                removeTaskFromSortedTreeSet(tasks.get(id));
                updateSchedule(tasks.get(id).getStartTime(), tasks.get(id).getEndTime(), false);

                tasks.remove(id);
                historyManager.remove(id);
                return true;
            } else {
                throw new IllegalArgumentException("Provided element 'id' " + id + "  not found");
            }

        } else {
            throw new NullPointerException("id object cannot be null");
        }
    }

    @Override
    public boolean deleteEpic(Integer id) {

        if (id != null) {
            if (epicTasks.containsKey(id)) {
                EpicTask epicTaskToDelete = epicTasks.get(id);
                List<SubTask> allSubTasksByEpic = getAllSubTasksByEpic(epicTaskToDelete);
                try {
                    allSubTasksByEpic.forEach(x -> {
                        deleteSubTask(x.getId());
                        removeTaskFromSortedTreeSet(x);
                        historyManager.remove(x.getId());
                        updateSchedule(x.getStartTime(), x.getEndTime(), false);

                    });
                } catch (Exception ex) {
                    createEpicTask(epicTaskToDelete);
                    allSubTasksByEpic.forEach(x -> {
                        createSubTask(x);
                        addTaskToSortedTreeSet(x);
                        historyManager.add(x);
                        updateSchedule(x.getStartTime(), x.getEndTime(), true);
                    });


                }


                epicTasks.remove(id);
                historyManager.remove(id);

            } else {
                throw new IllegalArgumentException("Provided element 'id' " + id + "  not found");
            }

        } else {
            throw new NullPointerException("id object cannot be null");
        }
        return true;
    }

    @Override
    public boolean deleteSubTask(Integer id) {


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
        return true;
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
            List<SubTask> subTaskstoReturn = new ArrayList<>();
            epic.getSubTasks().forEach(id -> {
                subTaskstoReturn.add(this.subTasks.get(id));
                historyManager.add(this.subTasks.get(id));
            });

            return subTaskstoReturn;
        } else {
            throw new NullPointerException("Tasks.Task object cannot be null");
        }

    }

    @Override
    public List<SubTask> getEpicSubTasks(Integer id) {
        if (id != null) {
            List<SubTask> collect = subTasks.entrySet()
                    .stream()
                    .filter(x -> x.getValue().getEpicId().equals(id))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            collect.forEach(historyManager::add);
            return collect;
        } else {
            return null;
        }
    }


}
