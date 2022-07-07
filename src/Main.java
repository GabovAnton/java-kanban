import managers.Managers;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatuses;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Посмотреть сериал", null, TaskStatuses.NEW.toString());

        Task task2 = new Task("Послушать музыку", null, TaskStatuses.IN_PROGRESS.toString());

        UUID taskid1 = taskManager.createTask(task1);

        UUID taskid2 = taskManager.createTask(task2);


        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания");

        //test creating task and return ID
        UUID epictaskid1 = taskManager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatuses.NEW.toString(),
                epictaskid1);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatuses.NEW.toString(),
                epictaskid1);

        SubTask subTask3 = new SubTask("Купить йогурт", "в Ашане", TaskStatuses.NEW.toString(),
                epictaskid1);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        EpicTask epicTask2 = new EpicTask("Купить продукты", null);


        //test creating task and return ID
        UUID epictaskid2 = taskManager.createEpicTask(epicTask2);


        //Print all tasks
        taskManager.printAllTasks();

        System.out.println("запрашиваем созданные задачи несколько раз в разном порядке;");
        //Test "get" method
        System.out.println("*****Печатаем историю до каких-либо просмотров******");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        taskManager.getStandaloneTask(taskid1);
        System.out.println("*****Печатаем историю после просмотра 1 задачи******");

        taskManager.getEpic(epictaskid1);
        System.out.println("*****Печатаем историю после просмотра 1 Эпика******");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        taskManager.getEpic(epictaskid2);
        System.out.println("*****Печатаем историю после просмотра 2 Эпика******");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        for (int i = 0; i < 10; i++) {
            taskManager.getEpic(epictaskid2);
        }

        taskManager.getEpic(epictaskid1);
        taskManager.getStandaloneTask(taskid2);

        System.out.println("*****Печатаем историю после нескольких  просмотров Эпиков и задач******");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);

        taskManager.deleteTask(taskid1);
        System.out.println("*****Печатаем историю после удаления задачи 'Посмотреть сериал'******");
        Managers.getDefaultHistory().getHistory().forEach(System.out::println);
        System.out.println("№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№");

        System.out.println("*****Печатаем  задачи ДО удаления эпика******");
        taskManager.printAllTasks();

        taskManager.deleteTask(epictaskid1);
        System.out.println("*****Печатаем  задачи после удаления эпика 'Выучить уроки'******");

        taskManager.printAllTasks();

    }

    private static void updateRandomSubtasks(TaskManager taskManager, UUID epictaskid) {
        EpicTask epicTask1Test = (EpicTask) taskManager.getTask(epictaskid);
        List<SubTask> subTasksToUpdate1 = taskManager.getAllSubTasksByEpic(epicTask1Test);

        subTasksToUpdate1.forEach(subTaskId -> {
            SubTask subTask = (SubTask) taskManager.getTask(subTaskId.getId());
            System.out.print("Обновляю subTaskId: " + subTaskId.getId() + "  [" + subTask.getStatus() + "  ->  "); //
            subTask.setStatus(getRandomStatusUsingNextInt(1, 4));
            System.out.println(subTask.getStatus() + "]");
            taskManager.updateSubTask(subTask);
        });
        taskManager.updateEpicStatus(epicTask1Test);
    }

    public static String getRandomStatusUsingNextInt(int min, int max) {
        Random random = new Random();
        int Status = random.nextInt(max - min) + min;
        switch (Status) {
            case (1):
                return TaskStatuses.NEW.toString();
            case (2):
                return TaskStatuses.IN_PROGRESS.toString();
            case (3):
                return TaskStatuses.DONE.toString();
            default:
                return "Error, while parsing statusName";
        }
    }

}