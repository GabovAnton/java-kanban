import managers.FileBackedTaskManagerTest;
import managers.InMemoryTaskManagerTest;
import managers.TaskManagerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author A.Gabov
 */
public class CommonTestClass {
    @BeforeAll
    public static void setupTestEnvironment() {

        TaskManagerTest fileBackedTaskManagerTest = new FileBackedTaskManagerTest();
        testTaskManager(fileBackedTaskManagerTest);

        TaskManagerTest inMemoryTaskManagerTest = new InMemoryTaskManagerTest();
        testTaskManager(inMemoryTaskManagerTest);
    }

    @Test
    public static void testTaskManager(TaskManagerTest taskmanager) {

        taskmanager.addNewTask();
        taskmanager.addEmptyEpicTask();
    }

}
