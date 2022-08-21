package webServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTasksManager;
import managers.HTTPTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import util.GsonAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private static Gson gson = new Gson();
    private static TaskManager taskManager =Managers.getHTTPTaskManager();
    static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        start();
        test();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private static void test() {

        LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        taskManager.fillTaskTimeSlots(startSchedulePeriod, Period.ofYears(1), Period.ofYears(1));


        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания", TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);


        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер", TaskStatus.NEW.toString(), epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(17), 5);
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

    }

    public static void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new Handler());
        httpServer.start();
    }

    public static void stop() {
        httpServer.stop(1);
    }

    private static boolean checkRequest(String method, HttpExchange httpExchange) {
        return httpExchange.getRequestMethod().equals(method) ? true : false;
    }

    private static Map<String, String> getParamMap(String IncomeQuery) {

        Optional<String> query = Optional.ofNullable(IncomeQuery);

        if (query.isEmpty()) {
            return Collections.emptyMap();
        } else {
            if (query.get().charAt(0) == '?') {
                query.get().substring(0, 1);
            }
        }

        return query.map(x -> x.split("&")).stream().flatMap(Arrays::stream).map(x -> x.split("=")).collect(Collectors.toMap(x -> x[0], x -> x[1]));

    }

    private static OptionalInt getIdFromQuery(Map<String, String> paramMap) {

        return paramMap.entrySet().stream().filter(e -> e.getKey().equals("id")).map(Map.Entry::getValue).mapToInt(Integer::parseInt).findFirst();
    }

    private static Optional<? extends Task> getTask(HttpExchange httpExchange, Class<? extends Task> taskType)
            throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        if (sb.length() > 0) {
            return Optional.ofNullable(gson.fromJson(sb.toString(), taskType));
        }
        return Optional.empty();
    }

    static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String endpoint = httpExchange.getRequestURI().getPath();
            Map<String, String> paramMap = getParamMap(httpExchange.getRequestURI().getQuery());
            OptionalInt id = getIdFromQuery(paramMap);
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                    .serializeNulls()
                    .create();
            switch (endpoint) {
                case "/tasks/task/":
                    handleTasks(httpExchange, paramMap);
                    break;
                case "/tasks/":
                    if (checkRequest("GET", httpExchange)) {
                        TreeSet prioritizedTasks = taskManager.getPrioritizedTasks();

                        try (OutputStream os = httpExchange.getResponseBody()) {
                            httpExchange.sendResponseHeaders(200, 0);
                            os.write(gson.toJson(prioritizedTasks).getBytes());
                            return;
                        }
                    }
                    break;
                case "/tasks/subtask/":
                    handleSubTasks(httpExchange, paramMap);
                    break;
                case "/tasks/epic/":
                    handleEpicTasks(httpExchange, paramMap);
                    break;
                case "/tasks/history":
                    if (checkRequest("GET", httpExchange)) {

                        ArrayList<Task> tasksHistory = taskManager.getHistoryManager().getHistory();
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            httpExchange.sendResponseHeaders(200, 0);
                            os.write(gson.toJson(tasksHistory).getBytes());
                            return;
                        }
                    }
                    break;
                case "/tasks/subtask/epic/":
                    if (checkRequest("GET", httpExchange)) {

                        List<SubTask> subTasks = taskManager.getEpicSubTasks(id.orElseThrow());
                        returnSimpleResponseCode(httpExchange, 404, gson.toJson(subTasks).getBytes());
                        return;
                    }
                    break;
                default:
                    System.out.println("Handler. Endpoint: " + endpoint + ". Sent Bad Request");
                    returnSimpleResponseCode(httpExchange, 404, null);
            }
        }

        private void returnSimpleResponseCode(HttpExchange httpExchange, int code, byte[] bytes) throws IOException {
            try (OutputStream os = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(code, 0);
                if (bytes.length > 0) {
                    os.write(bytes);
                }
                os.close();
                return;
            }
        }

        private void handleTasks(HttpExchange httpExchange, Map<String, String> paramMap) throws IOException {
            OptionalInt id = getIdFromQuery(paramMap);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                    .serializeNulls()
                    .create();
            if (checkRequest("GET", httpExchange)) {
                if (paramMap.isEmpty()) {
                    List<Task> tasks = taskManager.getTasks();


                    returnSimpleResponseCode(httpExchange, 200, gson.toJson(tasks).getBytes());
                } else {
                    Task task = taskManager.getStandaloneTask(id.orElseThrow());
                    returnSimpleResponseCode(httpExchange, 200, gson.toJson(task).getBytes());
                }
                return;
            } else if (checkRequest("POST", httpExchange)) {
                Optional<? extends Task> optionalTask = getTask(httpExchange, Task.class);
                if (optionalTask.isPresent()) {
                    Task task = optionalTask.get();
                    if (task.getId() != null) {
                        if (taskManager.updateTask(task)) {
                            returnSimpleResponseCode(httpExchange, 202, null);
                        } else {
                            returnSimpleResponseCode(httpExchange, 404, null);
                        }
                    } else {
                        Integer taskId = taskManager.createTask(task);
                        if (taskId != null) {
                            returnSimpleResponseCode(httpExchange, 201, null);
                            return;
                        } else {
                            returnSimpleResponseCode(httpExchange, 404, null);
                        }
                    }
                } else {
                    returnSimpleResponseCode(httpExchange, 404, null);
                }
            } else if (checkRequest("DELETE", httpExchange)) {
                if (paramMap.isEmpty()) {
                    if (taskManager.deleteAllTasks()) {
                        returnSimpleResponseCode(httpExchange, 204, null);
                    } else {
                        returnSimpleResponseCode(httpExchange, 404, null);
                    }
                } else if (id.isPresent()) {
                    if (taskManager.deleteTask(id.orElseThrow())) {
                        returnSimpleResponseCode(httpExchange, 200, null);
                        return;
                    } else {
                        returnSimpleResponseCode(httpExchange, 404, null);
                    }
                } else {
                    returnSimpleResponseCode(httpExchange, 404, null);
                }
            }
        }

        private void handleSubTasks(HttpExchange httpExchange, Map<String, String> paramMap) throws IOException {
            OptionalInt id = getIdFromQuery(paramMap);
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                    .serializeNulls()
                    .create();
            if (checkRequest("GET", httpExchange)) {
                if (paramMap.isEmpty()) {

                    List<SubTask> subTasks = taskManager.getSubtasks();
                    returnSimpleResponseCode(httpExchange, 200, gson.toJson(subTasks).getBytes());
                } else {
                    SubTask subTask = taskManager.getSubtask(id.orElseThrow());
                    returnSimpleResponseCode(httpExchange, 200, gson.toJson(subTask).getBytes());
                }
                return;
            } else if (checkRequest("POST", httpExchange)) {
                Optional<? extends Task> optionalSubTask = getTask(httpExchange, SubTask.class);
                if (optionalSubTask.isPresent()) {
                    SubTask subTask = (SubTask) optionalSubTask.get();
                    if (subTask.getId() != null) {
                        if (taskManager.updateSubTask(subTask)) {
                            returnSimpleResponseCode(httpExchange, 202, null);
                        } else {
                            returnSimpleResponseCode(httpExchange, 404, null);
                        }
                    } else {
                        Integer subTaskId = taskManager.createSubTask(subTask);
                        if (subTaskId != null) {
                            returnSimpleResponseCode(httpExchange, 201, null);
                            return;
                        } else {
                            returnSimpleResponseCode(httpExchange, 404, null);
                        }
                    }
                } else {
                    returnSimpleResponseCode(httpExchange, 404, null);
                }
            } else if (checkRequest("DELETE", httpExchange)) {
                if (paramMap.isEmpty()) {
                    if (taskManager.deleteAllSubTasks()) {
                        returnSimpleResponseCode(httpExchange, 204, null);
                    } else {
                        returnSimpleResponseCode(httpExchange, 404, null);
                    }
                } else if (id.isPresent()) {
                    if (taskManager.deleteSubTask(id.orElseThrow())) {
                        returnSimpleResponseCode(httpExchange, 200, null);
                        return;
                    } else {
                        returnSimpleResponseCode(httpExchange, 404, null);
                    }
                } else {
                    returnSimpleResponseCode(httpExchange, 404, null);
                }
            }
        }

        private void handleEpicTasks(HttpExchange httpExchange, Map<String, String> paramMap) throws IOException {
            OptionalInt id = getIdFromQuery(paramMap);
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                    .serializeNulls()
                    .create();
            if (checkRequest("GET", httpExchange)) {
                if (paramMap.isEmpty()) {

                    List<EpicTask> epicTasks = taskManager.getEpics();
                    returnSimpleResponseCode(httpExchange, 200, gson.toJson(epicTasks).getBytes());
                } else {
                    EpicTask epicTask = taskManager.getEpic(id.orElseThrow());
                    returnSimpleResponseCode(httpExchange, 200, gson.toJson(epicTask).getBytes());
                }
                return;

            } else if (checkRequest("POST", httpExchange)) {

                Optional<? extends Task> optionalEpicTask = getTask(httpExchange, EpicTask.class);

                if (optionalEpicTask.isPresent()) {
                    EpicTask epicTask = (EpicTask) optionalEpicTask.get();
                    if (epicTask.getId() != null) {
                        if (taskManager.updateEpicTask(epicTask)) {
                            returnSimpleResponseCode(httpExchange, 202, null);
                        } else {
                            returnSimpleResponseCode(httpExchange, 404, null);
                        }
                    } else {
                        Integer subTaskId = taskManager.createEpicTask(epicTask);
                        if (subTaskId != null) {
                            returnSimpleResponseCode(httpExchange, 201, null);
                            return;
                        } else {
                            returnSimpleResponseCode(httpExchange, 404, null);
                        }
                    }
                } else {
                    returnSimpleResponseCode(httpExchange, 404, null);
                }
            } else if (checkRequest("DELETE", httpExchange)) {

                if (paramMap.isEmpty()) {
                    if (taskManager.deleteAllEpicTasks()) {
                        returnSimpleResponseCode(httpExchange, 204, null);
                    } else {
                        returnSimpleResponseCode(httpExchange, 404, null);
                    }

                } else if (id.isPresent()) {
                    if (taskManager.deleteEpic(id.orElseThrow())) {
                        returnSimpleResponseCode(httpExchange, 200, null);
                        return;
                    } else {
                        returnSimpleResponseCode(httpExchange, 404, null);
                    }
                } else {
                    returnSimpleResponseCode(httpExchange, 404, null);
                }
            }
        }

    }
}


