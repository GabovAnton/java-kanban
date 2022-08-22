package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import util.GsonAdapter;
import util.KVTaskClient;
import util.ScheduleDateTimeCell;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class HTTPTaskManager extends FileBackedTasksManager {
    public static KVTaskClient kvTaskClient;

    public HTTPTaskManager(String httpAddress) {
        kvTaskClient = new KVTaskClient(httpAddress);
    }

    @Override
    public Task getStandaloneTask(Integer id) {
        return Optional.ofNullable(super.getStandaloneTask(id)).map(x -> {
                    saveHistory();
                    return x;
                })
                .orElse(null);
    }

    @Override
    public SubTask getSubtask(Integer id) {

        return Optional.ofNullable(super.getSubtask(id)).map(x -> {
                    saveHistory();
                    return x;
                })
                .orElse(null);
    }

    @Override
    public EpicTask getEpic(Integer id) {

        return Optional.ofNullable(super.getEpic(id)).map(x -> {
                    saveHistory();
                    return x;
                })
                .orElse(null);
    }

    private Consumer<? super Task> saveHistory() {
        try {
            kvTaskClient.put("history", historyToJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void save() throws ManagerSaveException {

        try {
            kvTaskClient.put("schedule", scheduleToJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            kvTaskClient.put("history", historyToJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            kvTaskClient.put("tasks", tasksToJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            kvTaskClient.put("subtasks", subTasksToJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            kvTaskClient.put("epictasks", epicTasksToJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
    }

    public void load() {
        restoreTasksCollection(kvTaskClient.load("tasks"));
        restoreEpicTasksCollection(kvTaskClient.load("epictasks"));
        restoreSubTasksCollection(kvTaskClient.load("subtasks"));
        restoreHistory(kvTaskClient.load("history"));
        restoreSchedule(kvTaskClient.load("schedule"));

    }

    @Override
    public Map<ScheduleDateTimeCell, Boolean> getSchedule() {
        final Map<ScheduleDateTimeCell, Boolean> schedule = new HashMap<>();
        schedule.putAll(super.getSchedule());
        return schedule;
    }

    private String scheduleToJson() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .create();
        Map<ScheduleDateTimeCell, Boolean> inMemorySchedule = this.getSchedule();

        return gson.toJson(inMemorySchedule);
    }

    private String historyToJson() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        HistoryManager historyManager = getHistoryManager();


        ArrayList<Task> taskInHistory = historyManager.getHistory();

        Type listTaskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        return gson.toJson(taskInHistory, listTaskType);

    }

    private String tasksToJson() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();

        List<Task> tasksInManager = getTasks();

        Type listTaskType = new TypeToken<List<Task>>() {
        }.getType();
        String s = gson.toJson(tasksInManager, listTaskType);
        return gson.toJson(tasksInManager, listTaskType);
    }

    private String subTasksToJson() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        List<SubTask> tasksInManager = getSubtasks();

        Type listTaskType = new TypeToken<List<SubTask>>() {
        }.getType();

        return gson.toJson(tasksInManager, listTaskType);
    }

    private String epicTasksToJson() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        List<EpicTask> tasksInManager = getEpics();

        Type listTaskType = new TypeToken<List<EpicTask>>() {
        }.getType();

        return gson.toJson(tasksInManager, listTaskType);
    }

    protected boolean restoreHistory(String historyJson) {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();
        HistoryManager historyManager = getHistoryManager();


        Type listTaskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        ArrayList<Task> taskHistoryFromJson = gson.fromJson(historyJson, listTaskType);
        if (taskHistoryFromJson != null) {
            taskHistoryFromJson.forEach(historyManager::add);
        }

        return true;
    }

    protected boolean restoreSchedule(String scheduleJson) {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();
        Type type = new TypeToken<HashMap<ScheduleDateTimeCell, Boolean>>() {
        }.getType();


        Map<ScheduleDateTimeCell, Boolean> scheduleFromJson = gson.fromJson(scheduleJson, type);
        final Map<ScheduleDateTimeCell, Boolean> scheduleInMemory = getSchedule();
        try {
            getSchedule().clear();
            getSchedule().putAll(scheduleFromJson);
        } catch (Exception ex) {
            getSchedule().clear();
            getSchedule().putAll(scheduleInMemory);
            return false;
        }
        return true;
    }

    protected boolean restoreTasksCollection(String tasksJson) {

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();

        Type listTaskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromJson = gson.fromJson(tasksJson, listTaskType);

        super.restoreTasksCollection(tasksFromJson);
        return true;
    }

    protected boolean restoreEpicTasksCollection(String epicTasksJson) {

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();

        Type listTaskType = new TypeToken<List<Task>>() {
        }.getType();
        List<EpicTask> epicTasksFromJson = gson.fromJson(epicTasksJson, listTaskType);
        super.restoreEpicTasksCollection(epicTasksFromJson);
        return true;
    }

    protected boolean restoreSubTasksCollection(String subTasksJson) {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();

        Type listTaskType = new TypeToken<List<Task>>() {
        }.getType();
        List<SubTask> subTasksFromJson = gson.fromJson(subTasksJson, listTaskType);
        super.restoreSubTasksCollection(subTasksFromJson);
        return true;
    }


}
