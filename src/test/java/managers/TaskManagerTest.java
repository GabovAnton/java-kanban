package managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    private T taskManager;

    @BeforeAll
    static void setupTestEnvironment() {
       // taskManager.fillTaskTimeSlots();
        //   private InMemoryHistoryManager historyManager = (InMemoryHistoryManager) taskManager.getHistoryManager();
    }

    public TaskManagerTest(T obj) {
        taskManager = obj;
    }

    @Test
    public void CheckIntersection() {

    }

    @Test
    public void addNewTask() {
        Task task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                LocalDateTime.now(), 15);

        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getStandaloneTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        //assertEquals(1, 4, "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addEmptyEpicTask() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString(), LocalDateTime.now(), 5);
        final int epicTaskId = taskManager.createEpicTask(epicTask);

        final EpicTask savedTask = taskManager.getEpic(epicTaskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epicTask, savedTask, "Задачи не совпадают.");

        final List<EpicTask> tasks = taskManager.getEpics();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
//        assertEquals(1, 4, "Неверное количество задач.");
        assertEquals(epicTask, tasks.get(0), "Задачи не совпадают.");

    }

@Test
    public void testSchedule (){
    taskManager.fillTaskTimeSlots();

    Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
            LocalDateTime.now(), 15);
    Task task2 = new Task("Послушать музыку", "jazz", TaskStatus.IN_PROGRESS.toString(),
            LocalDateTime.now(), 5);


    Integer taskId1 = taskManager.createTask(task1);

    Integer taskId2 = taskManager.createTask(task2);

    final Task savedTask1 = taskManager.getStandaloneTask(taskId1);
    final Task savedTask2 = taskManager.getStandaloneTask(taskId2);

    final List<Task> tasks = taskManager.getTasks();
    assertNotNull(tasks, "Задачи на возвращаются.");
    assertEquals(2, tasks.size(), "Неверное количество задач.");

    EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
            TaskStatus.IN_PROGRESS.toString(),  LocalDateTime.now(), 5);

    Integer epicTaskId1 = taskManager.createEpicTask(epicTask1);
    final Task savedEpicTask1 = taskManager.getEpic(epicTaskId1);

    SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
            TaskStatus.NEW.toString(),
            epicTaskId1, LocalDateTime.now(), 5);

    SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
            TaskStatus.NEW.toString(),
            epicTaskId1,  LocalDateTime.now(), 5);

    SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер", TaskStatus.NEW.toString(),
            epicTaskId1,  LocalDateTime.now(), 5);

    taskManager.createSubTask(subTask1);
    taskManager.createSubTask(subTask2);
    taskManager.createSubTask(subTask3);


    EpicTask epicTask2 = new EpicTask("Купить продукты", null, TaskStatus.IN_PROGRESS.toString(),
            LocalDateTime.now(), 5 );


    Integer epicTaskId2 = taskManager.createEpicTask(epicTask2);
    final Task savedEpicTask2 = taskManager.getEpic(epicTaskId2);

    final List<EpicTask> epicTasks = taskManager.getEpics();
    assertNotNull(epicTasks, "Эпики на возвращаются.");
    assertEquals(2, epicTasks.size(), "Неверное количество задач.");

    final List<SubTask> subTasks = taskManager.getSubtasks();
    assertNotNull(subTasks, "Подзадачи на возвращаются.");
    assertEquals(3, subTasks.size(), "Неверное количество задач.");


    Map<Map<LocalDateTime, LocalDateTime>, Boolean>  schedule =
            (Map<Map<LocalDateTime, LocalDateTime>, Boolean>) taskManager.getSchedule();

    TreeSet<Task> prioritizedTasks  = taskManager.getPrioritizedTasks();

    assertNotNull(prioritizedTasks, "Массив сортированных задач пуст");
    assertEquals(prioritizedTasks.size() > 0, "Массив сортированных задач пуст");

    assertNotNull(schedule, "Массив задач в расписании пуст");


    assertEquals(schedule.size()>0, "Массив задач в расписании пуст");

}
}



