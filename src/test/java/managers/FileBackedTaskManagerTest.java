package managers;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    public FileBackedTaskManagerTest() {
        super(new FileBackedTasksManager());
    }

}
