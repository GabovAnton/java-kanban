package managers;

import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    private final LocalDateTime startSchedulePeriod =
            LocalDateTime.of(2021, 8, 11, 14, 15);
    final static Path HOME_DIRECTORY = FileSystems.getDefault()
            .getPath("");
    final static String ARCHIVE_NAME = "canbanTest.csv";
    Path pathToFIle = HOME_DIRECTORY.resolve(ARCHIVE_NAME);


    public FileBackedTaskManagerTest() {
        super(new FileBackedTasksManager());
        fileBackedTasksManager.setPathToFIle(pathToFIle);
    }


    @Test
    public void saveFileShouldNoteReturnError() {

        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = fileBackedTasksManager.createEpicTask(epicTask);

        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, null, null);

        Task task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);

        fileBackedTasksManager.createTask(task);

        fileBackedTasksManager.createSubTask(subTask1);

         fileBackedTasksManager.createSubTask(subTask2);

        assertTrue(Files.exists(pathToFIle), "файл не существует");

    }

    @Test
    public void testSaveAndLoadFromFileToNewInmemoryManagerShouldNotReturnError() {
        EpicTask epicTask = new EpicTask("Выучить уроки", "Выполнить все домашние задания",
                TaskStatus.IN_PROGRESS.toString());
        final int epicTaskId = fileBackedTasksManager.createEpicTask(epicTask);
        SubTask subTask1 = new SubTask("Выучить стихотворение Лермонтова", "Тучи",
                TaskStatus.NEW.toString(),
                epicTaskId, startSchedulePeriod.minusYears(1).plusMinutes(1), 5);

        SubTask subTask2 = new SubTask("Выучить стихотворение Есенина", "Письмо к женщине",
                TaskStatus.NEW.toString(),
                epicTaskId, null, null);

        Task task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.minusYears(1).plusMinutes(16), 1);

         fileBackedTasksManager.createTask(task);

         fileBackedTasksManager.createSubTask(subTask1);

        fileBackedTasksManager.createSubTask(subTask2);

        assertTrue(Files.exists(pathToFIle), "файл не существует");


            InMemoryTaskManager testInmemoryTaskManager = new InMemoryTaskManager();
            assertDoesNotThrow(() -> fileBackedTasksManager.loadFromFile(HOME_DIRECTORY.resolve(ARCHIVE_NAME).toFile(),
                    testInmemoryTaskManager));

    }

    @Test
    public void testSaveAndLoadFromFileToExistingManagerShouldReturnError() {

            saveFileShouldNoteReturnError();
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () ->
                            fileBackedTasksManager.
                                    loadFromFile(HOME_DIRECTORY.resolve(ARCHIVE_NAME).toFile(), fileBackedTasksManager));

            assertEquals(IllegalArgumentException.class, exception.getClass());


    }


}
