import java.util.Random;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task();
        task1.setStatus(1);
        task1.setName("Посмотреть сериал");

        Task task2 = new Task();
        task2.setStatus(2);
        task2.setName("Послушать музыку");
        UUID TaskID1 = taskManager.createTask(task1);
        UUID TaskID2 = taskManager.createTask(task2);


        EpicTask epicTask1 = new EpicTask();
        epicTask1.setName("Выучить уроки");
        epicTask1.setDescription("Выполнить все домашние задания");

        SubTask subTask1 = new SubTask();
        subTask1.setName("Выучить стихотворение Лермонтова");
        subTask1.setDescription("Тучи");
        subTask1.setStatus(1);

        SubTask subTask2 = new SubTask();
        subTask2.setName("Выучить стихотворение Есенина ");
        subTask2.setDescription("Письмо к женщине");
        subTask2.setStatus(1);

        epicTask1.addSubTasks(subTask1);
        epicTask1.addSubTasks(subTask2);

        //test creating task and return ID
        UUID EpicTaskID1 = taskManager.createEpicTask(epicTask1);

        EpicTask epicTask2 = new EpicTask();
        epicTask2.setName("Купить продукты");

        SubTask subTask3 = new SubTask();
        subTask3.setName("Хлеб");
        subTask3.setDescription("Белый с отрубями");
        subTask3.setStatus(1);

        epicTask2.addSubTasks(subTask3);

        //test creating task and return ID
        UUID EpicTaskID2 = taskManager.createEpicTask(epicTask2);
        //Print all tasks
        taskManager.getTaskList().forEach(x -> System.out.println(x));

        System.out.println("Update subtask statuses...");
        //Test "get" method and randomly update each subTask status in EpicTask1 to test "update" method
        EpicTask epicTask1Test = (EpicTask) taskManager.getTask(EpicTaskID1);
        taskManager.getAllSubTasksByEpic(epicTask1Test).forEach(x -> {
            x.setStatus(getRandomNumberUsingNextInt(1, 4));
        });
        TaskManager.updateEpicStatus(epicTask1Test);

        //Test "get" method and randomly update each subTask status in EpicTask2 to test "update" method
        EpicTask epicTask2Test = (EpicTask) taskManager.getTask(EpicTaskID2);
        taskManager.getAllSubTasksByEpic(epicTask2).forEach(x -> {
            x.setStatus(getRandomNumberUsingNextInt(1, 4));
        });
        TaskManager.updateEpicStatus(epicTask2Test);

        taskManager.getTask(TaskID1).setStatus(3);
        taskManager.getTask(TaskID2).setStatus(1);

        System.out.println("*****new statuses******");


        taskManager.getTaskList().forEach(x -> {
            System.out.println(x.toString());
        });


        taskManager.deleteTask(EpicTaskID1);
        taskManager.deleteTask(TaskID1);

        System.out.println("*****after deleting******");
        taskManager.getTaskList().forEach(x -> {
            System.out.println(x.toString());
        });

    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}