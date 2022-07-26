package managers;

import Exceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author A.Gabov
 */
public class FileBackedTasksManager extends InMemoryTaskManager {
    final static Path HOME_DIRECTORY = FileSystems.getDefault()
            .getPath("");
    final static String ARCHIVE_NAME = "canban.csv";

    public static void main(String[] args) throws ManagerSaveException {
        loadFromFile(HOME_DIRECTORY.resolve(ARCHIVE_NAME).toFile());
    }

    public static void loadFromFile(File file) throws ManagerSaveException {
        Path pathToFIle = HOME_DIRECTORY.resolve(ARCHIVE_NAME);
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = (InMemoryHistoryManager) taskManager.getHistoryManager();
        try {
            String content = Files.readString(pathToFIle);
            boolean isTask = true;
            String[] stringArray = content.split(System.lineSeparator());
            for (int i = 1; i < stringArray.length; i++) {
                String str = stringArray[i];
                if (isTask) {
                    if (!str.isEmpty() && str != "\\r" && str != "\\n") {
                        Task task = fromString(str);
                        if (task instanceof SubTask) {
                            taskManager.createSubTask((SubTask) task);
                        } else if (task instanceof EpicTask) {
                            taskManager.createEpicTask((EpicTask) task);
                        } else {
                            taskManager.createTask(task);
                        }
                    } else {
                        isTask = false;
                    }
                } else {
                    List<Integer> history = historyFromString(str);
                    history.forEach(id -> {
                        Task task = getTaskFromId(id, taskManager);
                        inMemoryHistoryManager.add(task);
                    });
                }

            }


        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    private static Task getTaskFromId(int id, TaskManager taskManager) {
        Task task = null;
        if (taskManager.getEpics().stream().filter(x -> x.getId() == id).findFirst().isPresent()) {
            task = taskManager.getEpics().stream().filter(x -> x.getId() == id).findFirst().get();
        } else if (taskManager.getTasks().stream().filter(x -> x.getId() == id).findFirst().isPresent()) {
            task = taskManager.getTasks().stream().filter(x -> x.getId() == id).findFirst().get();
        } else if (taskManager.getSubtasks().stream().filter(x -> x.getId() == id).findFirst().isPresent()) {
            task = taskManager.getSubtasks().stream().filter(x -> x.getId() == id).findFirst().get();
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
        Arrays.stream(value.split(",")).forEach(x -> {
            taskList.add(Integer.parseInt(x));
        });
        return taskList;
    }

    static Task fromString(String value) {
        List<String> tasks = List.of(value.split(","));

        Task newTask;
        if (tasks.get(1).equals(TaskType.TASK.name())) {
            newTask = new Task(tasks.get(2), tasks.get(4), tasks.get(3), Integer.parseInt(tasks.get(0)));
            return newTask;
        } else if (tasks.get(1).equals(TaskType.EPIC.name())) {
            newTask = new EpicTask(tasks.get(2), tasks.get(4), tasks.get(3), Integer.parseInt(tasks.get(0)));
            return newTask;
        } else if (tasks.get(1).equals(TaskType.SUBTASK.name())) {
            newTask = new SubTask(tasks.get(2), tasks.get(4), tasks.get(3), Integer.parseInt(tasks.get(5)),
                    Integer.parseInt(tasks.get(0)));
            return newTask;
        }

        return null;
    }

    public void save() throws ManagerSaveException {
        Path pathToFIle = HOME_DIRECTORY.resolve(ARCHIVE_NAME);

        List<String> stringList = new ArrayList<>();

        stringList.add("id,type,name,status,description,epic");
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
        list.forEach(x -> {
            listToSave.add(x.toString());
        });
    }

    @Override
    public void updateEpicStatus(EpicTask task) {
        super.updateEpicStatus(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Integer createEpicTask(EpicTask task) {
        int id = super.createEpicTask(task);
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
    public void updateEpicTask(EpicTask task) {
        super.updateEpicTask(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
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
