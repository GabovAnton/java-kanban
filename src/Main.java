import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Посмотреть сериал", null, "NEW");

        Task task2 = new Task("Послушать музыку", null, "IN_PROGRESS");

        UUID taskid1 = taskManager.createTask(task1);

        UUID taskid2 = taskManager.createTask(task2);


        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания");

        //test creating task and return ID
        UUID epictaskid1 = taskManager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи", "NEW",
                epictaskid1);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине", "NEW",
                epictaskid1);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);


        EpicTask epicTask2 = new EpicTask("Купить продукты", null);


        //test creating task and return ID
        UUID epictaskid2 = taskManager.createEpicTask(epicTask2);
        SubTask subTask3 = new SubTask("Хлеб", "Белый с отрубями", "NEW", epictaskid2);
        taskManager.createSubTask(subTask3);


        //Print all tasks
        taskManager.getTaskList().forEach(System.out::println);

        System.out.println("Update subtask statuses...");
        //Test "get" method and randomly update each subTask status in EpicTask1 to test "update" method
        EpicTask epicTask1Test = (EpicTask) taskManager.getTask(epictaskid1);
        ArrayList<SubTask> subTasksToUpdate1 = taskManager.getAllSubTasksByEpic(epicTask1Test);

        subTasksToUpdate1.forEach(x -> {

            x.setStatus(getRandomStatusUsingNextInt(1, 4));
            taskManager.updateSubTask(x);
        });
        taskManager.updateEpicStatus(epicTask1Test);

        //Test "get" method and randomly update each subTask status in EpicTask2 to test "update" method
        EpicTask epicTask2Test = (EpicTask) taskManager.getTask(epictaskid2);

        ArrayList<SubTask> subTasksToUpdate2 = taskManager.getAllSubTasksByEpic(epicTask2Test);
        subTasksToUpdate2.forEach(x -> {
            x.setStatus(getRandomStatusUsingNextInt(1, 4));
            taskManager.updateSubTask(x);
        });
        taskManager.updateEpicStatus(epicTask2Test);

        Task taskToUpdate1 = taskManager.getTask(taskid1);
        taskToUpdate1.setStatus("DONE");
        taskManager.updateTask(taskToUpdate1);

        Task taskToUpdate2 = taskManager.getTask(taskid2);
        taskToUpdate2.setStatus("NEW");
        taskManager.updateTask(taskToUpdate2);

        EpicTask epicTaskToUpdate = (EpicTask) taskManager.getTask(epictaskid1);
        epicTaskToUpdate.setDescription("New description");
        taskManager.updateEpicTask(epicTaskToUpdate);


        System.out.println("*****new statuses******");


        taskManager.getTaskList().forEach(x -> System.out.println(x.toString()));


        taskManager.deleteTask(epictaskid1);
        taskManager.deleteTask(taskid1);

        System.out.println("*****after deleting******");
        taskManager.getTaskList().forEach(x -> System.out.println(x.toString()));
        System.out.println("*****delete all******");
        taskManager.deleteAllTasks();

    }

    public static String getRandomStatusUsingNextInt(int min, int max) {
        Random random = new Random();
        int Status = random.nextInt(max - min) + min;
        switch (Status) {
            case (1):
                return "NEW";
            case (2):
                return "IN_PROGRESS";
            case (3):
                return "DONE";
            default:
                return "Error, while parsing statusName";
        }
    }

}