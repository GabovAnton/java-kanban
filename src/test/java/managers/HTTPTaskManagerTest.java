package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import util.GsonAdapter;
import util.KVServer;
import util.ScheduleDateTimeCell;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static managers.HTTPTaskManager.kvTaskClient;
import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest {
    private  static TaskManager taskManager;
    static KVServer kvServer;
    static Integer epicId;
    static Integer task1Id;
    static Integer task2Id;
    static Integer subTask3Id;

    static {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void setupEnvironment() {
        kvServer.start();
        taskManager = Managers.getHTTPTaskManager();;
    }

    @BeforeEach
    void createTasks() {
        epicId = taskManager.getCurrentMaxTaskId() + 10001;
        task1Id = taskManager.getCurrentMaxTaskId() + 10002;
        task2Id = taskManager.getCurrentMaxTaskId() + 10003;
        subTask3Id = taskManager.getCurrentMaxTaskId() + 10004;
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        taskManager.fillTaskTimeSlots(startSchedulePeriod, Period.ofYears(1), Period.ofYears(1));


        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);


        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(17), 5);
        subTask3.setId(subTask3Id);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        Task task = new Task("Посмотреть сериал", "WestWorld", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(1), 15);

        task.setId(task1Id);

        Task task2 = new Task("Посмотреть фильм", "Тарантино", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(30), 15);
        task2.setId(task2Id);
        taskManager.createTask(task);
        taskManager.createTask(task2);
    }

    @AfterEach
    void renewEnvironment() {

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicTasks();
        taskManager.deleteAllSubTasks();
        taskManager.getHistoryManager().getHistory().clear();

    }

    @Test
    public void testSerializationAndDeserializationScheduleInMemoryTaskManagerShouldReturnError() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .create();
        final Map<ScheduleDateTimeCell, Boolean> inMemorySchedule = new HashMap<>();
        if (taskManager instanceof HTTPTaskManager) {
            inMemorySchedule.putAll(((HTTPTaskManager) taskManager).getSchedule());
        } else {
            fail("класс Менеджера задач не  верно определен");
        }
        List<Map.Entry> filledSchedule = new ArrayList<>(inMemorySchedule.entrySet().stream()
                .filter(x -> x.getValue().equals(true)).collect(Collectors.toList()));

        Type type = new TypeToken<HashMap<ScheduleDateTimeCell, Boolean>>() {
        }.getType();
        String out = gson.toJson(inMemorySchedule);


        Map<ScheduleDateTimeCell, Boolean> scheduleLoaded = gson.fromJson(out, type);

        List<Map.Entry> filledScheduleFromDeserialization = scheduleLoaded
                .entrySet()
                .stream()
                .filter(x -> x.getValue().equals(true))
                .collect(Collectors.toList());

        List<Map.Entry> ScheduleArrayDifferences = filledSchedule.stream()
                .filter(element -> !filledScheduleFromDeserialization.contains(element))
                .collect(Collectors.toList());


        assertTrue(ScheduleArrayDifferences.isEmpty(),
                "Данные после сериализации/десериализации не совпадают");
    }

    @Test
    public void testSerializationAndDeserializationHistoryManagerShouldReturnError() {

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())

                .serializeNulls()
                .create();
        taskManager.getStandaloneTask(100);
        taskManager.getStandaloneTask(101);
        taskManager.getSubtask(103);

        HistoryManager historyManager = taskManager.getHistoryManager();


        ArrayList<Task> taskInHistory = historyManager.getHistory();

        Type listTaskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        String out = gson.toJson(taskInHistory, listTaskType);
        ArrayList<Task> taskHistoryFromJson = gson.fromJson(out, listTaskType);

        List<Task> taskHistoryArrayDifferences = taskInHistory.stream()
                .filter(element -> !taskHistoryFromJson.contains(element))
                .collect(Collectors.toList());
        assertTrue(taskHistoryArrayDifferences.isEmpty(),
                "Данные после сериализации/десериализации не совпадают");
    }

    @Test
    public void testSerializationAndDeserializationTaskCollectionFromManagerShouldReturnError() {

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        List<Task> tasksInManager = taskManager.getTasks();

        Type listTaskType = new TypeToken<List<Task>>() {
        }.getType();

        String out = gson.toJson(tasksInManager, listTaskType);
        List<Task> tasksFromJson = gson.fromJson(out, listTaskType);

        List<Task> taskHistoryArrayDifferences = tasksInManager.stream()
                .filter(element -> !tasksFromJson.contains(element))
                .collect(Collectors.toList());
        assertTrue(taskHistoryArrayDifferences.isEmpty(),
                "Данные после сериализации/десериализации не совпадают");
    }

    @Test
    public void testSerializationAndDeserializationEpicTaskCollectionFromManagerShouldReturnError() {

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        List<EpicTask> tasksInManager = taskManager.getEpics();

        Type listTaskType = new TypeToken<List<EpicTask>>() {
        }.getType();

        String out = gson.toJson(tasksInManager, listTaskType);
        List<EpicTask> tasksFromJson = gson.fromJson(out, listTaskType);

        List<EpicTask> taskHistoryArrayDifferences = tasksInManager.stream()
                .filter(element -> !tasksFromJson.contains(element))
                .collect(Collectors.toList());
        assertTrue(taskHistoryArrayDifferences.isEmpty(),
                "Данные после сериализации/десериализации не совпадают");
        //  taskManager.restoreTasksCollection(tasksFromJson);
    }

    @Test
    public void testSerializationAndDeserializationSubTaskCollectionFromManagerShouldReturnError() {

        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        List<SubTask> tasksInManager = taskManager.getSubtasks();

        Type listTaskType = new TypeToken<List<SubTask>>() {
        }.getType();

        String out = gson.toJson(tasksInManager, listTaskType);
        List<SubTask> tasksFromJson = gson.fromJson(out, listTaskType);

        List<SubTask> taskHistoryArrayDifferences = tasksInManager.stream()
                .filter(element -> !tasksFromJson.contains(element))
                .collect(Collectors.toList());
        assertTrue(taskHistoryArrayDifferences.isEmpty(),
                "Данные после сериализации/десериализации не совпадают");
        //  taskManager.restoreTasksCollection(tasksFromJson);
    }

    @Test
    public void testSaveDataToKVServer() {

        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        Task task = new Task("Посмотреть сериал", "WestWorld", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(135), 15);
      int newTaskId =  taskManager.createTask(task);
     //   taskManager = new HTTPTaskManager("http://localhost:8078");
        List<Task> tasksFromServer = getTasksFromJson(kvTaskClient.load("tasks"));

        assertTrue(tasksFromServer.stream().anyMatch(x->x.getId().equals(newTaskId)),
                "Даннные не были загружены с сервера");
    }

    @Test
    public void testLoadDataFromKVServer() {

         taskManager = new HTTPTaskManager("http://localhost:8078");
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        taskManager.fillTaskTimeSlots(startSchedulePeriod, Period.ofYears(1), Period.ofYears(1));


        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания", TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);


        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(17), 5);
        subTask3.setId(subTask3Id);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        Integer subTask3Id = taskManager.createSubTask(subTask3);

        Task task = new Task("Посмотреть сериал", "WestWorld", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(1), 15);

        Task task2 = new Task("Посмотреть фильм", "Тарантино", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(30), 15);
        final int taskId = taskManager.createTask(task);
        final int taskId2 = taskManager.createTask(task2);

        taskManager.getStandaloneTask(taskId);
        taskManager.getStandaloneTask(taskId2);
        taskManager.getEpic(epicTaskId);
        taskManager.getSubtask(subTask3Id);

        if (taskManager instanceof HTTPTaskManager) {
            assertTrue(getTasksFromJson(kvTaskClient.load("tasks")).size() > 0,
                    "Даннные не были загружены с сервера");
            assertTrue(getSubTasksFromJson(kvTaskClient.load("subtasks")).size() > 0,
                    "Даннные не были загружены с сервера");
            assertTrue(getEpicTasksFromJson(kvTaskClient.load("epictasks")).size() > 0,
                    "Даннные не были загружены с сервера");
            assertTrue(getScheduleFromJson(kvTaskClient.load("schedule")).size() > 0,
                    "Даннные не были загружены с сервера");
            assertTrue(getHistoryFromJson(kvTaskClient.load("history")).size() > 0,
                    "Даннные не были загружены с сервера");
        } else {
            fail("класс Менеджера задач не правильно определен");
        }
    }

    ArrayList<Task> getHistoryFromJson(String historyJson) {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                .serializeNulls()
                .create();


        Type listTaskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        ArrayList<Task> taskHistoryFromJson = gson.fromJson(historyJson, listTaskType);


        return taskHistoryFromJson;
    }

    Map<ScheduleDateTimeCell, Boolean> getScheduleFromJson(String scheduleJson) {
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

        return scheduleFromJson;
    }

    List<Task> getTasksFromJson(String tasksJson) {

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


        return tasksFromJson;
    }

    List<EpicTask> getEpicTasksFromJson(String epicTasksJson) {

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

        return epicTasksFromJson;
    }

    List<SubTask> getSubTasksFromJson(String subTasksJson) {
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
        return subTasksFromJson;
    }
}
