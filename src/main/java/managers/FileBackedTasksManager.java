package managers;

import exceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;
import util.ScheduleDateTimeCell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author A.Gabov
 */
public class FileBackedTasksManager extends InMemoryTaskManager {
    final static Path HOME_DIRECTORY = FileSystems.getDefault()
            .getPath("");
    final static String ARCHIVE_NAME = "canban.csv";

    Path pathToFIle = HOME_DIRECTORY.resolve(ARCHIVE_NAME);

    public void setPathToFIle(Path pathToFIle) {
        this.pathToFIle = pathToFIle;

    }

    @Override
    protected Map<ScheduleDateTimeCell, Boolean> getSchedule() {
        return super.getSchedule();
    }

    public void loadFromFile(File file, InMemoryTaskManager inmemoryTaskManagerToLoad) throws ManagerSaveException {
        InMemoryHistoryManager inMemoryHistoryManagerToLoad = (InMemoryHistoryManager) inmemoryTaskManagerToLoad.getHistoryManager();
        inmemoryTaskManagerToLoad.getSchedule().clear();

        try {
            String content = Files.readString(file.toPath());
            boolean isTask = true;
            int maxId = 0;
            String[] stringArray = content.split("\\R");
            for (int i = 1; i < stringArray.length; i++) {
                String str = stringArray[i];
                if (isTask) {
                    if (!str.isEmpty() && !str.equals("\\r") && !str.equals("\\n")) {
                        Task task = fromString(str);
                        maxId = (task != null ? task.getId() : null)
                                != null ? task.getId() > maxId ? task.getId() : maxId : maxId;
                        if (task instanceof SubTask) {
                            inmemoryTaskManagerToLoad.createSubTask((SubTask) task);
                            System.out.println("subTask id: " + task.getId() + ": loaded");
                        } else if (task instanceof EpicTask) {
                            inmemoryTaskManagerToLoad.createEpicTask((EpicTask) task);
                            System.out.println("epictask id: " + task.getId() + ": loaded");

                        } else {
                            inmemoryTaskManagerToLoad.createTask(task);
                            System.out.println("task id: " + (task != null ? task.getId() : null) + ": loaded");

                        }
                    } else {
                        isTask = false;
                    }
                } else {
                    List<Integer> history = historyFromString(str);
                    history.forEach(id -> {
                        Task task = getTaskFromId(id, inmemoryTaskManagerToLoad);
                        inMemoryHistoryManagerToLoad.add(task);
                    });
                }

            }
            setInitialId(maxId);

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    private static Task getTaskFromId(int id, TaskManager taskManager) {
        Task task = null;
        if (taskManager.getEpics().stream().anyMatch(x -> x.getId() == id)) {
            task = taskManager.getEpics().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        } else if (taskManager.getTasks().stream().anyMatch(x -> x.getId() == id)) {
            task = taskManager.getTasks().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        } else if (taskManager.getSubtasks().stream().anyMatch(x -> x.getId() == id)) {
            task = taskManager.getSubtasks().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        }

        return task;
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        manager.getHistory().forEach(task -> {
            sb.append(task.getId());
            sb.append(",");
        });
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.toString().length() - 1);
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> taskList = new ArrayList<>();
        Arrays.stream(value.split(",")).forEach(x -> taskList.add(Integer.parseInt(x)));
        return taskList;
    }

    static Task fromString(String value) {
        List<String> tasks = List.of(value.split(","));

        Task newTask;
        Optional<LocalDateTime> startDateTime = tasks.get(5).isBlank() || tasks.get(5).equals("' '") ? Optional.empty() :
                Optional.of(LocalDateTime.parse(tasks.get(5), Task.getFormatter()));

        Optional<Integer> duration = tasks.get(6).isBlank() || tasks.get(6).equals("' '") ? Optional.empty() :
                Optional.of(Integer.parseInt(tasks.get(6)));

        if (tasks.get(1).equals(TaskType.TASK.name())) {
            newTask = new Task(tasks.get(2), tasks.get(4), tasks.get(3),
                    Integer.parseInt(tasks.get(0)),
                    startDateTime.orElse(null), duration.orElse(null));

            return newTask;
        } else if (tasks.get(1).equals(TaskType.EPIC.name())) {
            newTask = new EpicTask(tasks.get(2), tasks.get(4), tasks.get(3), Integer.parseInt(tasks.get(0)));

            return newTask;
        } else if (tasks.get(1).equals(TaskType.SUBTASK.name())) {
            newTask = new SubTask(tasks.get(2),
                    tasks.get(4),
                    tasks.get(3),
                    Integer.parseInt(tasks.get(7)),
                    Integer.parseInt(tasks.get(0)),
                    startDateTime.isPresent() ? (startDateTime.orElse(null)) : null,
                    duration.isPresent() ? (duration.orElse(null)) : null
            );


            return newTask;
        }

        return null;
    }

    public void save() throws ManagerSaveException {


        List<String> stringList = new ArrayList<>();

        stringList.add("id,type,name,status,description,epic, startDateTime, duration");
        appendTasksFromCollection(stringList, super.getTasks());
        appendTasksFromCollection(stringList, super.getEpics());
        appendTasksFromCollection(stringList, super.getSubtasks());
        stringList.add(historyToString(historyManager));

        try {
            Files.write(pathToFIle, stringList);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

    }

    private void appendTasksFromCollection(List<String> listToSave, List<? extends Task> list) {
        list.forEach(x -> listToSave.add(x.toString()));
    }

    @Override
    public void updateEpicStatus(EpicTask epicTask) {
        super.updateEpicStatus(epicTask);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public boolean deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
            return true;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
            return true;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        try {
            save();
            return true;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    @Override
    public Integer createEpicTask(EpicTask epicTask) {
        int id = super.createEpicTask(epicTask);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return id;
    }

    @Override
    public Integer createSubTask(SubTask task) {
        int id = super.createSubTask(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return id;
    }

    @Override
    public Integer createTask(Task task) {
        int id = super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return id;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        try {
            save();
            return true;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
            return true;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        super.updateTask(task);
        try {

            save();
            return true;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteTask(Integer id) {
        if (super.deleteTask(id)) {
            try {
                save();
            } catch (ManagerSaveException exception) {
                System.out.println(exception.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteEpic(Integer id) {
        if (super.deleteEpic(id)) {
            try {
                save();
            } catch (ManagerSaveException exception) {
                System.out.println(exception.getMessage());
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean deleteSubTask(Integer id) {
        if (super.deleteSubTask(id)) {
            try {
                save();
            } catch (ManagerSaveException exception) {
                System.out.println(exception.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }
}
