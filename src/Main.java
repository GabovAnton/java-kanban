import managers.FileBackedTasksManager;
import managers.InMemoryHistoryManager;
import managers.Managers;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.TaskStatus;
import tasks.SubTask;
import tasks.Task;

public class Main {


    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = (InMemoryHistoryManager) taskManager.getHistoryManager();
        Task task1 = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString());

        Task task2 = new Task("Послушать музыку", null, TaskStatus.IN_PROGRESS.toString());

        Integer taskId1 = taskManager.createTask(task1);

        Integer taskId2 = taskManager.createTask(task2);


        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания");

        Integer epicTaskId1 = taskManager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId1);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId1);

        SubTask subTask3 = new SubTask("Выучить стихотворение А. Блока", "Летний вечер", TaskStatus.NEW.toString(),
                epicTaskId1);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        EpicTask epicTask2 = new EpicTask("Купить продукты", null);


        Integer epicTaskId2 = taskManager.createEpicTask(epicTask2);

        System.out.println("*****Печатаем историю до каких-либо просмотров******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);


        System.out.println("*****Печатаем историю после просмотра 1 задачи******");
        taskManager.getStandaloneTask(taskId1);
        inMemoryHistoryManager.getHistory().forEach(System.out::println);

        taskManager.getEpic(epicTaskId1);
        System.out.println("*****Печатаем историю после просмотра 1 Эпика******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);


        taskManager.getEpic(epicTaskId2);
        System.out.println("*****Печатаем историю после просмотра 2 Эпика******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);
        System.out.println("Печатаем все ссылки в CustomLinkedList");
        inMemoryHistoryManager.printHistoryLinks();

        System.out.println("*****Печатаем историю после просмотра 1 Эпика 'id:" + epicTaskId1 + "' еще раз******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);

        taskManager.getEpic(epicTaskId1);

        System.out.println("*****Печатаем историю после хаотичных просмотров******");
        taskManager.getStandaloneTask(taskId2);
        taskManager.getEpic(epicTaskId1);
        taskManager.getStandaloneTask(taskId2);
        taskManager.getSubtask(5);
        inMemoryHistoryManager.getHistory().forEach(System.out::println);


        taskManager.deleteTask(taskId1);
        System.out.println("*****Печатаем историю после удаления задачи 'Посмотреть сериал'******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);
        System.out.println("№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№");


        taskManager.deleteEpic(epicTaskId1);
        String str = "";
        System.out.println("*****Печатаем  все ссылки в CustomLinkedList  после удаления эпика '1'******");
        inMemoryHistoryManager.printHistoryLinks();
        inMemoryHistoryManager.getHistory().forEach(System.out::println);


        taskManager.deleteEpic(epicTaskId2);

        System.out.println("*****Печатаем все ссылки в CustomLinkedList  после удаления эпика '2'******");
        inMemoryHistoryManager.printHistoryLinks();
        inMemoryHistoryManager.getHistory().forEach(System.out::println);


    }


}