package managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import tasks.EpicTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    private  T taskManager;

    @BeforeAll
    static void setupTestEnvironment() {
     //   private InMemoryHistoryManager historyManager = (InMemoryHistoryManager) taskManager.getHistoryManager();
    }

    public TaskManagerTest(T obj){
        this.taskManager = obj;
    }


    @Test
    public  void addNewTask() {
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
    public void addEmptyEpicTask(){
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString(),  LocalDateTime.now(), 5);
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

}

