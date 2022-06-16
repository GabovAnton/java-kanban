import TaskManagers.Managers;
import TaskManagers.TaskManager;
import Tasks.EpicTask;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.TaskStatuses;

import java.util.ArrayList;
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

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи", TaskStatuses.NEW.toString(),
                epictaskid1);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине", TaskStatuses.NEW.toString(),
                epictaskid1);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);


        EpicTask epicTask2 = new EpicTask("Купить продукты", null);


        //test creating task and return ID
        UUID epictaskid2 = taskManager.createEpicTask(epicTask2);
        SubTask subTask3 = new SubTask("Хлеб", "Белый с отрубями", TaskStatuses.NEW.toString(), epictaskid2);
        taskManager.createSubTask(subTask3);


        //Print all tasks
        taskManager.printAllTasks();

        System.out.println("Update subtask statuses...");
        //Test "get" method and randomly update each subTask status in EpicTask1 to test "update" method
        EpicTask epicTask1Test = (EpicTask) taskManager.getTask(epictaskid1);
        ArrayList<SubTask> subTasksToUpdate1 = taskManager.getAllSubTasksByEpic(epicTask1Test);

        subTasksToUpdate1.forEach(subTaskId -> {
            SubTask subTask = (SubTask) taskManager.getTask(subTaskId.getId());
            System.out.print("Обновляю subTaskId: " + subTaskId.getId() + "  [" + subTask.getStatus() + "  ->  "); //
            subTask.setStatus(getRandomStatusUsingNextInt(1, 4));
            System.out.println(subTask.getStatus() + "]");
            taskManager.updateSubTask(subTask);
        });
        taskManager.updateEpicStatus(epicTask1Test);

        //Test "get" method and randomly update each subTask status in EpicTask2 to test "update" method
        EpicTask epicTask2Test = (EpicTask) taskManager.getTask(epictaskid2);

        ArrayList<SubTask> subTasksToUpdate2 = taskManager.getAllSubTasksByEpic(epicTask2Test);
        subTasksToUpdate2.forEach(subTaskId -> {
            SubTask subTask = (SubTask) taskManager.getTask(subTaskId.getId());
            System.out.print("Обновляю subTaskId: " + subTaskId.getId() + "  [" + subTask.getStatus() + "  ->  "); //
            subTask.setStatus(getRandomStatusUsingNextInt(1, 4));
            System.out.println(subTask.getStatus() + "]");
            taskManager.updateSubTask(subTask);
        });
        taskManager.updateEpicStatus(epicTask2Test);

        Task taskToUpdate1 = taskManager.getTask(taskid1);
        taskToUpdate1.setStatus(TaskStatuses.DONE.toString());
        taskManager.updateTask(taskToUpdate1);

        Task taskToUpdate2 = taskManager.getTask(taskid2);
        taskToUpdate2.setStatus(TaskStatuses.NEW.toString());
        taskManager.updateTask(taskToUpdate2);

        EpicTask epicTaskToUpdate = (EpicTask) taskManager.getTask(epictaskid1);
        epicTaskToUpdate.setDescription("New description");
        taskManager.updateEpicTask(epicTaskToUpdate);

        System.out.println("*****new statuses******");

        taskManager.printAllTasks();


        taskManager.deleteTask(epictaskid1);
        taskManager.deleteTask(taskid1);

        //test get query's for History
        for (int i = 0; i < 5; i++) {
            taskManager.getTaskList().forEach(task ->{
                if (task instanceof EpicTask) {
                    taskManager.getEpic(task.getId());
                } else if (task instanceof  SubTask) {
                    taskManager.getSubtask(task.getId());
                } else {
                    taskManager.getStandaloneTask(task.getId());
                }
            });
        }

        System.out.println("*****after deleting******");
        taskManager.printAllTasks();
        System.out.println("*****delete all******");
        taskManager.deleteAllTasks();
        taskManager.printAllTasks();

        System.out.println("*****print history******");

        Managers.getDefaultHistory().getHistory().forEach(System.out::println);
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