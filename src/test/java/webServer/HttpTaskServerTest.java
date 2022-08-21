package webServer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class HttpTaskServerTest {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson = new Gson();
    private static TaskManager taskManager;
    static List<Integer> correctAnswers = List.of(200, 201, 202, 203, 204);

    HttpClient client = HttpClient.newHttpClient();
    String address = "http://localhost:8080";
    static KVServer kvServer;

    static {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static int taskId;
    static int subTaskId;
    static int epicTaskId;

    @BeforeAll
    static void setupEnvironment() throws IOException {

        kvServer.start();
        taskManager = Managers.getHTTPTaskManager();
        HttpTaskServer.start();

    }

    @BeforeEach
    void addTasks() {
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания", TaskStatus.IN_PROGRESS.toString());
        epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(17), 5);

        subTaskId = taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        Task task = new Task("Посмотреть сериал", "WestWorld", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(1), 15);

        Task task2 = new Task("Посмотреть фильм", "Тарантино", TaskStatus.NEW.toString(), startSchedulePeriod.plusDays(30), 15);

        taskId = taskManager.createTask(task);
        final int taskId2 = taskManager.createTask(task2);
    }

    @AfterEach
    void renewEnvironment() {

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicTasks();
        taskManager.deleteAllSubTasks();
        taskManager.getHistoryManager().getHistory().clear();

    }

    @Test
    void testGetTaskTask() {

        URI url = URI.create(address + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем, успешно ли обработан запрос
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                JsonArray TasksArray = jsonElement.getAsJsonArray();
                GsonBuilder builder = new GsonBuilder();

                Gson gson = builder
                        .enableComplexMapKeySerialization()
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                        .serializeNulls()
                        .create();

                Type taskType = new TypeToken<List<Task>>() {
                }.getType();

                List<Task> tasksFromJson = gson.fromJson(TasksArray, taskType);
                assertEquals(2, tasksFromJson.size(),
                        "неправильное количество задач получено от сервера");

            } else {
                fail("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            fail("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void testPostTaskTask() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/task/");
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        Task task = new Task("Посмотреть сериал", "WestWorld", TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

        Type type = new TypeToken<Task>() {
        }.getType();

        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task, type)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testPostTaskTaskId() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/task/?id=" + taskId);
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        Task task = new Task("Измененная задача", "Suits", TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

        Type type = new TypeToken<Task>() {
        }.getType();

        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task, type)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testDeleteTaskTaskId() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testDeleteTaskTask() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testGetTask() {
        URI url = URI.create(address + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                assertEquals(5, jsonArray.size(),
                        "неправильное количество приоритетных задач получено от сервера");

            } else {
                fail("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            fail("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");

        }
    }

    @Test
    void testGetTaskHistory() {
        taskManager.getStandaloneTask(taskId);
        taskManager.getEpic(epicTaskId);
        URI url = URI.create(address + "/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                assertEquals(2, jsonArray.size(),
                        "неправильное количество приоритетных задач получено от сервера");

            } else {
                fail("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            fail("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");

        }
    }

    @Test
    void testGetTaskTaskId() {

        URI url = URI.create(address + "/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                assertFalse(jsonElement.isJsonNull(), "Ответ от сервера не соответствует ожидаемому.");
                GsonBuilder builder = new GsonBuilder();

                Gson gson = builder
                        .enableComplexMapKeySerialization()
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                        .serializeNulls()
                        .create();

                Type TaskType = new TypeToken<Task>() {
                }.getType();
                Task taskFromJson = gson.fromJson(jsonElement, TaskType);
                assertEquals(taskId,taskFromJson.getId(),
                        "id запрошенного элемента и полученного не совпадают");
            } else {
                fail("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
          fail("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void testGetTaskSubTaskId() {
        URI url = URI.create(address + "/tasks/subtask/?id=" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());

                assertFalse(jsonElement.isJsonNull(), "Ответ от сервера не соответствует ожидаемому.");
                GsonBuilder builder = new GsonBuilder();

                Gson gson = builder
                        .enableComplexMapKeySerialization()
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                        .serializeNulls()
                        .create();

                Type TaskType = new TypeToken<SubTask>() {
                }.getType();
                SubTask taskFromJson =gson.fromJson(jsonElement, TaskType);
                assertEquals(subTaskId,taskFromJson.getId(),
                        "id запрошенного элемента и полученного не совпадают");

            } else {
                fail("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            fail("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void testGetEpicTaskId() {
        URI url = URI.create(address + "/tasks/epic/?id=" + epicTaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                assertFalse(jsonElement.isJsonNull(), "Ответ от сервера не соответствует ожидаемому.");
                GsonBuilder builder = new GsonBuilder();

                Gson gson = builder
                        .enableComplexMapKeySerialization()
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                        .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                        .serializeNulls()
                        .create();

                Type TaskType = new TypeToken<EpicTask>() {
                }.getType();
                EpicTask taskFromJson =gson.fromJson(jsonElement, TaskType);
                assertEquals(epicTaskId,taskFromJson.getId(),
                        "id запрошенного элемента и полученного не совпадают");
            } else {
                fail("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
           fail("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void testPostTaskEpic() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/epic/");
        EpicTask epicTask = new EpicTask("Новый эпик", "без описания",
                TaskStatus.IN_PROGRESS.toString());


        Type type = new TypeToken<EpicTask>() {
        }.getType();

        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicTask, type)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testPostTaskSubTask() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/subtask/");
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        SubTask subTask = new SubTask("Новая подзадача", "без описания",
                TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);



        Type type = new TypeToken<SubTask>() {
        }.getType();

        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask, type)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testPostTaskEpicId() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/epic/?id=" + epicTaskId);
        EpicTask epicTask = new EpicTask("Измененный эпик", "без описания",
                TaskStatus.IN_PROGRESS.toString());


        Type type = new TypeToken<EpicTask>() {
        }.getType();

        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicTask, type)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testPostTaskSubTaskId() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/subtask/?id=" + subTaskId);
        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        SubTask subTask = new SubTask("Измененная подзадача", "без описания",
                TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);



        Type type = new TypeToken<SubTask>() {
        }.getType();

        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask, type)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testDeleteTaskSubtaskId() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/subtask/?id=" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testDeleteTaskEpicId() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/epic/?id=" + epicTaskId);
        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testDeleteTaskSubTask() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

    @Test
    void testDeleteTaskEpic() throws IOException, InterruptedException {
        URI url = URI.create(address + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder(url)
                .header("content-type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(correctAnswers.contains(response.statusCode()),
                "Ответ от сервера содержит неправильный код");
    }

}