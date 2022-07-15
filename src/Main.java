import managers.InMemoryHistoryManager;
import managers.Managers;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.StatusTask;
import tasks.SubTask;
import tasks.Task;

public class Main {


    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = (InMemoryHistoryManager) taskManager.getHistoryManager();

        Task task1 = new Task("Посмотреть сериал", null, StatusTask.NEW.toString());

        Task task2 = new Task("Послушать музыку", null, StatusTask.IN_PROGRESS.toString());

        Integer taskId1 = taskManager.createTask(task1);

        Integer taskId2 = taskManager.createTask(task2);


        EpicTask epicTask1 = new EpicTask("Выучить уроки", "Выполнить все домашние задания");

        Integer epicTaskId1 = taskManager.createEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                StatusTask.NEW.toString(),
                epicTaskId1);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                StatusTask.NEW.toString(),
                epicTaskId1);

        SubTask subTask3 = new SubTask("Купить йогурт", "в Ашане", StatusTask.NEW.toString(),
                epicTaskId1);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);


        EpicTask epicTask2 = new EpicTask("Купить продукты", null);


        Integer epicTaskId2 = taskManager.createEpicTask(epicTask2);


        taskManager.printAllTasks();

        System.out.println("запрашиваем созданные задачи несколько раз в разном порядке;");

        System.out.println("*****Печатаем историю до каких-либо просмотров******");

        taskManager.getStandaloneTask(taskId1);
        System.out.println("*****Печатаем историю после просмотра 1 задачи******");
        inMemoryHistoryManager.printHistoryLinks();

        taskManager.getEpic(epicTaskId1);
        System.out.println("*****Печатаем историю после просмотра 1 Эпика******");
        inMemoryHistoryManager.printHistoryLinks();


        taskManager.getEpic(epicTaskId2);
        System.out.println("*****Печатаем историю после просмотра 2 Эпика******");
        inMemoryHistoryManager.printHistoryLinks();


        System.out.println("*****Печатаем историю после просмотра 2 Эпика  еще раз!!!!!!!!!!******");

        taskManager.getEpic(epicTaskId2);
        inMemoryHistoryManager.printHistoryLinks();


        taskManager.getEpic(epicTaskId1);
        taskManager.getStandaloneTask(taskId2);

        System.out.println("*****Печатаем историю после нескольких  просмотров Эпиков и задач******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);
        inMemoryHistoryManager.printHistoryLinks();


        taskManager.deleteTask(taskId1);
        System.out.println("*****Печатаем историю после удаления задачи 'Посмотреть сериал'******");
        inMemoryHistoryManager.getHistory().forEach(System.out::println);
        System.out.println("№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№");

        System.out.println("*****Печатаем все ссылки в CustomLinkedList  задачи ДО удаления эпика******");
        inMemoryHistoryManager.printHistoryLinks();


        taskManager.deleteTask(epicTaskId1);
        System.out.println("*****Печатаем  все ссылки в CustomLinkedList  после удаления эпика 'Выучить уроки'******");


        System.out.println("*****Печатаем все ссылки в CustomLinkedList  после удаления эпика 'Выучить уроки'******");
        inMemoryHistoryManager.printHistoryLinks();

    }


}