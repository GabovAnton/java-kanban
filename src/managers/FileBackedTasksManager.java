package managers;

import Exceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class FileBackedTasksManager extends InMemoryTaskManager {
    final static Path HOME_DIRECTORY = FileSystems.getDefault()
            .getPath("");
    final static String ARCHIVE_NAME = "canban.csv";

    public void save() {
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
            e.printStackTrace();
            //throw new ManagerSaveException(exception.getMessage());

        }
               /* String content = Files.readString(pathToFIle);
                for (String str : content.split(System.lineSeparator())) {
                    System.out.println(str);
                }*/


        //  System.out.println(sb);
    }

    public static void loadFromFile(File file) {
        Path pathToFIle = HOME_DIRECTORY.resolve(ARCHIVE_NAME);
        try {
            String content = Files.readString(pathToFIle);
            for (String str : content.split(System.lineSeparator())) {
                System.out.println(str);
            }
        }
        catch (IOException exception) {
            //throw new ManagerSaveException(exception.getMessage());
        }
    }

    private void appendTasksFromCollection(List<String> listToSave, List<? extends Task> list) {
        list.forEach(x -> {
            listToSave.add(x.toString());
            // sb.append(System.lineSeparator());
        });
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

        return taskList;
    }


    private void convertToString(Task task) {

    }

    Task fromString(String value) {
        //TODO написать реализацию для каждого типа задач
        Task newTask = new Task("test", "test Description");

        return newTask;
    }

    @Override
    public void updateEpicStatus(EpicTask task) {
        super.updateEpicStatus(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public Integer createEpicTask(EpicTask task) {
        int id = super.createEpicTask(task);
        save();
        return id;
    }

    @Override
    public Integer createSubTask(SubTask task) {
        int id = super.createSubTask(task);
        save();
        return id;
    }

    @Override
    public Integer createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateEpicTask(EpicTask task) {
        super.updateEpicTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public boolean deleteTask(Integer id) {
        if (super.deleteTask(id)) {
            save();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteEpic(Integer id) {
        if (super.deleteEpic(id)) {
            save();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean deleteSubTask(Integer id) {
        if (super.deleteSubTask(id)) {
            save();
            return true;
        } else {
            return false;
        }
    }
}
