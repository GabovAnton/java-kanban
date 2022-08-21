package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    private final T taskManager;
    private LocalDateTime startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
    private final int yearsBeforeStart = 1;
    private final int yearsAfterEnd = 2;


    @BeforeEach
    void setupTestEnvironment() {
        taskManager.fillTaskTimeSlots(startSchedulePeriod, Period.ofYears(yearsBeforeStart),
                Period.ofYears(yearsAfterEnd));
    }

    public TaskManagerTest(T obj) {
        taskManager = obj;
    }

    @Test
    public void testAddOneNewTask() {
        Task task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getStandaloneTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testAddEmptyEpicTask() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        final EpicTask savedTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epicTask, savedTask, "Задачи не совпадают.");

        final List<EpicTask> epicTasks = taskManager.getEpics();
        assertNotNull(epicTasks, "Задачи на возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество задач.");
        assertEquals(epicTask, epicTasks.get(0), "Задачи не совпадают.");

    }

    @Test
    public void testAddEpicTaskWithSubtasksInStatusNEW() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 5);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        final EpicTask savedEpicTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedEpicTask, "Задача не найдена.");
        assertEquals(epicTask, savedEpicTask, "Задачи не совпадают.");

        final List<EpicTask> epicTasks = taskManager.getEpics();
        assertNotNull(epicTasks, "Задачи на возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, epicTasks.get(0), "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(3, savedEpicTask.getSubTasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в менеджере.");
        assertEquals(TaskStatus.NEW.toString(), savedEpicTask.getStatus(), "Статус в эпике не верный.");

        subTasks.forEach(subTask -> assertNotNull(subTask.getEpicId(), "ЭпикID пустой"));
        assertTrue(savedEpicTask.getDuration().isPresent(), "В эпике пустое поле длительность");
        assertEquals(15, savedEpicTask.getDuration().get(), "Неверная длительность в эпике.");


    }

    @Test
    public void testAddEpicTaskWithSubtasksInStatusDONE() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.DONE.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.DONE.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер",
                TaskStatus.DONE.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 5);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        final EpicTask savedEpicTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedEpicTask, "Задача не найдена.");
        assertEquals(epicTask, savedEpicTask, "Задачи не совпадают.");

        final List<EpicTask> epicTasks = taskManager.getEpics();
        assertNotNull(epicTasks, "Задачи на возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, epicTasks.get(0), "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(3, savedEpicTask.getSubTasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в менеджере.");
        assertEquals(TaskStatus.DONE.toString(), savedEpicTask.getStatus(), "Статус в эпике не верный.");

        subTasks.forEach(subTask -> assertNotNull(subTask.getEpicId(), "Эпик ID пустой"));
        assertTrue(savedEpicTask.getDuration().isPresent(), "В эпике пустое поле длительность");
        assertEquals(15, savedEpicTask.getDuration().get(), "Неверная длительность в эпике.");


    }

    @Test
    public void testAddEpicTaskWithSubtasksInStatusNEWAndDONE() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.NEW.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.DONE.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 5);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        final EpicTask savedEpicTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedEpicTask, "Задача не найдена.");
        assertEquals(epicTask, savedEpicTask, "Задачи не совпадают.");

        final List<EpicTask> epicTasks = taskManager.getEpics();
        assertNotNull(epicTasks, "Задачи на возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, epicTasks.get(0), "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(3, savedEpicTask.getSubTasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в менеджере.");
        assertEquals(TaskStatus.IN_PROGRESS.toString(), savedEpicTask.getStatus(), "Статус в эпике не верный.");

        subTasks.forEach(subTask -> assertNotNull(subTask.getEpicId(), "Эпик ID пустой"));
        assertTrue(savedEpicTask.getDuration().isPresent(), "В эпике пустое поле длительность");

        assertEquals(15, savedEpicTask.getDuration().get(), "Неверная длительность в эпике.");


    }

    @Test
    public void testAddEpicTaskWithSubtasksInStatusIN_PROGRESS() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.NEW.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.IN_PROGRESS.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.IN_PROGRESS.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер",
                TaskStatus.IN_PROGRESS.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 5);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        final EpicTask savedEpicTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedEpicTask, "Задача не найдена.");
        assertEquals(epicTask, savedEpicTask, "Задачи не совпадают.");

        final List<EpicTask> epicTasks = taskManager.getEpics();
        assertNotNull(epicTasks, "Задачи на возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, epicTasks.get(0), "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(3, savedEpicTask.getSubTasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в менеджере.");
        assertEquals(TaskStatus.IN_PROGRESS.toString(), savedEpicTask.getStatus(), "Статус в эпике не верный.");

        subTasks.forEach(subTask -> assertNotNull(subTask.getEpicId(), "Эпик ID пустой"));
        assertTrue(savedEpicTask.getDuration().isPresent(), "В эпике пустое поле длительность");

        assertEquals(15, savedEpicTask.getDuration().get(), "Неверная длительность в эпике.");


    }

    @Test
    public void testSubtaskIfEpicIdExist() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.NEW.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.DONE.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 5);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        final EpicTask savedEpicTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedEpicTask, "Задача не найдена.");
        assertEquals(epicTask, savedEpicTask, "Задачи не совпадают.");

        final List<EpicTask> epicTasks = taskManager.getEpics();
        assertNotNull(epicTasks, "Задачи на возвращаются.");
        assertEquals(1, epicTasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, epicTasks.get(0), "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubtasks();

        subTasks.forEach(subTask -> assertNotNull(subTask.getEpicId(), "Эпик ID пустой"));

        assertEquals(3, savedEpicTask.getSubTasks().size(), "Неверное количество подзадач в эпике.");
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в менеджере.");
        assertEquals(TaskStatus.IN_PROGRESS.toString(), savedEpicTask.getStatus(), "Статус в эпике не верный.");
        assertTrue(savedEpicTask.getDuration().isPresent(), "В эпике пустое поле длительность");

        assertEquals(15, savedEpicTask.getDuration().get(), "Неверная длительность в эпике.");


    }

    @Test
    public void testAddEpicTaskWithSubtasksAndIncorrectDurationShouldReturnException() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), null);


         taskManager.createSubTask(subTask1);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> taskManager.createSubTask(subTask2));
        assertEquals("Duration and Start should be both filled up or both empty", exception.getMessage());


    }

    @Test
    public void testAddEpicTaskWithSubtasksAndIncorrectStartShouldReturnException() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, null, 5);


         taskManager.createSubTask(subTask1);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> taskManager.createSubTask(subTask2));
        assertEquals("Duration and Start should be both filled up or both empty", exception.getMessage());


    }

    @Test
    public void testAddEpicTestShouldNotReturnExceptionOfTwoTaskWithEmptyDurationAndStart() {

        // public EpicTask(String name, String description, String status, Optional<LocalDateTime> startTime) {

        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());

        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, null, null);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 5);


        taskManager.createSubTask(subTask1);
        assertDoesNotThrow(() -> {
            taskManager.createSubTask(subTask2);
        });
        taskManager.createSubTask(subTask3);

        final EpicTask savedEpicTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedEpicTask, "эпик' не найден.");
        assertEquals(epicTask, savedEpicTask, "эпики не совпадают.");
        assertTrue(savedEpicTask.getDuration().isPresent(), "В эпике пустое поле длительность");

        assertEquals(10, savedEpicTask.getDuration().get(), "Неверная длительность в эпике.");

        final List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(3, subTasks.size(), "Неверное количество подзадач в менеджере.");

    }

    @Test
    public void testShouldNotReturnExceptionOfTwoTaskFollowingOneByOne() {
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                LocalDateTime.of(2022, 8, 12, 14, 15), 15);
        Task task2 = new Task("Послушать музыку", "jazz", TaskStatus.IN_PROGRESS.toString(),

                LocalDateTime.of(2022, 8, 12, 14, 30), 5);
        Integer taskId1 = taskManager.createTask(task1);

        Integer taskId2 = taskManager.createTask(task2);
        taskManager.getStandaloneTask(taskId1);
        taskManager.getStandaloneTask(taskId2);
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(2, tasks.size(), "Неверное количество задачв массиве");
        if (taskManager instanceof InMemoryTaskManager) {
            assertNotNull(((InMemoryTaskManager) taskManager).getSchedule(), "Массив задач в расписании пуст");
        } else {
            fail("класс Менеджера задач не определен");
        }

    }

    @Test
    public void testShouldReturnExceptionOfInIntersectingTaskWithEqualStartTime() {
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                LocalDateTime.of(2022, 8, 12, 14, 15), 15);

        Task task2 = new Task("Послушать музыку", "jazz", TaskStatus.IN_PROGRESS.toString(),
                LocalDateTime.of(2022, 8, 12, 14, 15), 5);

        taskManager.createTask(task1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                taskManager.createTask(task2));
        assertEquals("Provided date range for Task id: " + task2.getId() +
                " name(Послушать музыку) is intersects with exiting one", exception.getMessage());
    }

    @Test
    public void testShouldReturnExceptionOfTasksWithIntersectingEndAndStartTime() {
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                LocalDateTime.of(2022, 8, 12, 14, 15), 15);
        Task task2 = new Task("Послушать музыку", "jazz", TaskStatus.IN_PROGRESS.toString(),

                LocalDateTime.of(2022, 8, 12, 14, 29), 5);
         taskManager.createTask(task1);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
        assertEquals("Provided date range for Task id: " + task2.getId() +
                " name(Послушать музыку) is intersects with exiting one", exception.getMessage());
    }

    @Test
    public void testShouldReturnExceptionWhenAddTaskWithStartBeforeSchedulePeriod() {
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.minusYears(10).minusMinutes(16), 16);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task1));

        assertEquals("Provided date range for Task is is less then start date in Schedule",
                exception.getMessage());
    }

    @Test
    public void testShouldReturnExceptionWhenAddTaskWithEndAfterSchedulePeriod() {
        LocalDateTime taskStart = startSchedulePeriod.plusYears(yearsAfterEnd).plusMinutes(3);

        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                taskStart, 15);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task1));

        assertEquals("Provided date range for Task is is greater then start date in Schedule",
                exception.getMessage());
    }

    @Test
    public void testUpdateEpicStatus() {

        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.NEW.toString());

        final int epicTaskId = taskManager.createEpicTask(epicTask);


        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, null, null);

        final int subTaskId1 = taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);


        final SubTask savedSubTask1 = taskManager.getSubtask(subTaskId1);

        savedSubTask1.setStatus(String.valueOf(TaskStatus.IN_PROGRESS));

        final EpicTask savedEpic = taskManager.getEpic(epicTaskId);

        savedEpic.setName("НОВОЕ ИМЯ");
        savedEpic.setDescription("НОВОЕ ОПИСАНИЕ");

        taskManager.updateEpicStatus(savedEpic);
        final EpicTask updatedEpic = taskManager.getEpic(epicTaskId);


        assertEquals(TaskStatus.IN_PROGRESS.toString(), updatedEpic.getStatus(), "Статус в эпике не верный.");
        assertEquals("НОВОЕ ИМЯ", updatedEpic.getName(), "Эпик не обновляет поля");
        assertEquals("НОВОЕ ОПИСАНИЕ", updatedEpic.getDescription(), "Эпик не обновляет поля");
    }


    @Test
    public void testGetTasksVoidShouldReturn3() {

        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                null, null);

         taskManager.createTask(task1);

        Task task2 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

          taskManager.createTask(task2);

        Task task3 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(100), 15);

        taskManager.createTask(task3);


        var Tasks = taskManager.getTasks();

        assertEquals(3, Tasks.size(), "Неверное количество задач сохранено в менеджере");
    }

    @Test
    public void testGetEpicsVoidShouldReturn2() {

        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());

         taskManager.createEpicTask(epicTask);

        EpicTask epicTask2 = new EpicTask("Выучить уроки2", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());

         taskManager.createEpicTask(epicTask2);

        assertEquals(2, taskManager.getEpics().size(), "Неверное количество задач сохранено в менеджере");
    }

    @Test
    public void testAddSubtaskInStatusDONEWithoutEpicShouldReturnException() {
        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.DONE.toString(),
                null, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        NullPointerException exception =
                assertThrows(NullPointerException.class, () -> taskManager.createSubTask(subTask1));
        assertEquals("EpicID in subtask object cannot be null", exception.getMessage());

    }

    @Test
    public void testAddSubtaskInStatusDONEWithEpicShouldNotReturnException() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());

        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        assertDoesNotThrow(() -> {
            taskManager.createSubTask(subTask1);
        });


    }


    @Test
    public void testAddTaskInStatusDONEShouldReturnNotException() {
        Task task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 15);
        assertDoesNotThrow(() -> {
            taskManager.createTask(task);
        });
    }

    @Test
    public void testUpdateEpicTaskShouldReturnNotException() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        assertDoesNotThrow(() -> {
            taskManager.createEpicTask(epicTask);
        });
    }

    @Test
    public void testUpdateSubTaskShouldNotReturnException() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());

        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        final int subTaskId = taskManager.createSubTask(subTask);

        SubTask updatedSubTask = taskManager.getSubtask(subTaskId);

        updatedSubTask.setName("UPDATED");

        assertDoesNotThrow(() -> taskManager.updateSubTask(updatedSubTask));

        final SubTask savedSubTaskAfterUpdate = taskManager.getSubtask(subTaskId);

        assertEquals("UPDATED", savedSubTaskAfterUpdate.getName(),
                "не обновились поля в подзадаче");
    }

    @Test
    public void testDeleteTaskShouldNotReturnException() {
        Task task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 15);

        final Integer taskId = taskManager.createTask(task);
        assertEquals(task, taskManager.getStandaloneTask(taskId));

        assertDoesNotThrow(() -> {
            taskManager.deleteTask(taskId);
        });

        assertNull(taskManager.getStandaloneTask(taskId));

    }

    @Test
    public void testDeleteAllTasksShouldNotReturnException() {
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(17), 15);
        Task task2 = new Task("Посмотреть сериал2", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 15);

        final Integer taskId1 = taskManager.createTask(task1);
        assertEquals(task1, taskManager.getStandaloneTask(taskId1));

        final Integer taskId2 = taskManager.createTask(task2);
        assertEquals(task2, taskManager.getStandaloneTask(taskId2));

        assertDoesNotThrow(taskManager::deleteAllTasks);

        assertNull(taskManager.getStandaloneTask(taskId1));

    }

    @Test
    public void testDeleteAllSubTasksShouldNotReturnNException() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        final int subTaskId1 = taskManager.createSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubtask(subTaskId1));

        final Integer subTaskId2 = taskManager.createSubTask(subTask2);
        assertEquals(subTask2, taskManager.getSubtask(subTaskId2));

        assertDoesNotThrow(taskManager::deleteAllSubTasks);

        assertNull(taskManager.getSubtask(subTaskId2), "подзадача осталась в менеджере");

    }

    @Test
    public void testDeleteAllEpicTaskShouldNotReturnNException() {
        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId1 = taskManager.createEpicTask(epicTask1);


        EpicTask epicTask2 = new EpicTask("Выучить уроки2", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());

        final int epicTaskId2 = taskManager.createEpicTask(epicTask2);


        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId1, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId2, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        final int subTaskId1 = taskManager.createSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubtask(subTaskId1));

        final Integer subTaskId2 = taskManager.createSubTask(subTask2);
        assertEquals(subTask2, taskManager.getSubtask(subTaskId2));

        assertDoesNotThrow(taskManager::deleteAllEpicTasks);

        assertNull(taskManager.getSubtask(subTaskId2), "подзадача осталась в менеджере");

        assertNull(taskManager.getEpic(epicTaskId2), "эпик остался в менеджере");

    }

    @Test
    public void testDeleteEpicTaskShouldNotReturnNException() {
        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId1 = taskManager.createEpicTask(epicTask1);
        assertEquals(epicTask1, taskManager.getEpic(epicTaskId1));

        assertDoesNotThrow(() -> {
            taskManager.deleteEpic(epicTaskId1);
        });

        assertNull(taskManager.getEpic(epicTaskId1), "эпик остался в менеджере");

    }

    @Test
    public void testDeleteSubTaskShouldNotReturnNException() {
        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId1 = taskManager.createEpicTask(epicTask1);

        assertEquals(epicTask1, taskManager.getEpic(epicTaskId1));

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId1, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        final int subTaskId1 = taskManager.createSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubtask(subTaskId1), "подзадачи не равны");

        assertDoesNotThrow(() -> {
            taskManager.deleteSubTask(subTaskId1);
        });

        assertNull(taskManager.getSubtask(subTaskId1), "подзадача осталась в менеджере");
        assertFalse(taskManager.getEpic(epicTaskId1).getSubTasks().contains(subTaskId1),
                "подзадача осталась в эпике");
    }

    @Test
    public void testGetAllSubtasksByEpicShouldNotReturnNException() {
        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId1 = taskManager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId1, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId1, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(1), 5);

        final int subTaskId1 = taskManager.createSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubtask(subTaskId1), "подзадачи не равны");

        final Integer subTaskId2 = taskManager.createSubTask(subTask2);
        assertEquals(subTask2, taskManager.getSubtask(subTaskId2), "подзадачи не равны");

        final EpicTask savedEpic = taskManager.getEpic(epicTaskId1);

        var subtasks = taskManager.getAllSubTasksByEpic(epicTask1);

        assertEquals(savedEpic.getSubTasks().size(), subtasks.size(), "подзадачи в эпике и менеджере не равны");

    }

    @Test
    public void testSetInitialID() {
        taskManager.setInitialId(100);
        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId1 = taskManager.createEpicTask(epicTask1);
        final EpicTask savedEpic = taskManager.getEpic(epicTaskId1);

        assertEquals(101, savedEpic.getId(), "неверный ID задачи");
    }

    @Test
    public void testSetFillSchedule() {
        startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 0, 0);
        taskManager.fillTaskTimeSlots(startSchedulePeriod, Period.ofYears(0),
                Period.ofDays(1));

        if (taskManager instanceof InMemoryTaskManager) {
            assertEquals(24 * 4 + 1, ((InMemoryTaskManager)taskManager).getSchedule().size(),
                    "неверный размер массива schedule");
        } else {
            fail("класс Менеджера задач не определен");
        }
    }

    @Test
    public void testPrioritizedSortedTaskRetrievalOrder() {
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                null, null);

        final int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

        final int taskId2 = taskManager.createTask(task2);

        Task task3 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(100), 15);

         taskManager.createTask(task3);

        TreeSet<Task> sortedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Неверный количество задач в sortedTasks");

        assertEquals(taskId2, sortedTasks.first().getId(), "Неверный порядок сортировки sortedTasks");
        assertEquals(taskId1, sortedTasks.last().getId(), "Неверный порядок сортировки sortedTasks");

    }

    @Test
    public void testGetSchedule() {
        if (taskManager instanceof InMemoryTaskManager) {
            assertTrue(((InMemoryTaskManager)taskManager).getSchedule().size() > 0,
                    "расписание пустое");
        } else {
            fail("класс Менеджера задач не определен");
        }

    }

    @Test
    public void testToString() {

        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                null, null);
        Task task2 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId1 = taskManager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId1, startSchedulePeriod.minusYears(yearsBeforeStart).plusMinutes(100), 5);

        assertDoesNotThrow(() -> {
            System.out.println(task1);
            System.out.println(task2);
            System.out.println(epicTask1);
            System.out.println(subTask1);
        });

    }


}



