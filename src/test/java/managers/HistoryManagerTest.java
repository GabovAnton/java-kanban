package managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static TaskManager taskManager;
    private static InMemoryHistoryManager inMemoryHistoryManager;
    private static Task task;
    private static LocalDateTime startSchedulePeriod;

    @BeforeAll
    public static void setupEnvironment() {
        taskManager = Managers.getDefault();
        startSchedulePeriod = LocalDateTime.of(2021, 8, 11, 14, 15);
        taskManager.fillTaskTimeSlots(startSchedulePeriod, Period.ofYears(1), Period.ofYears(1));
        inMemoryHistoryManager = (InMemoryHistoryManager) taskManager.getHistoryManager();


    }

    @BeforeEach
    public void setup() {
        task = new Task("Посмотреть сериал", null, TaskStatus.NEW.toString(),
                startSchedulePeriod.plusDays(1), 15);
        task.setId(100);
        inMemoryHistoryManager.add(task);

    }

    @Test
    void testAddShouldNotReturnException() {
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void testGetHistoryShouldNotReturnException() {
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История  пустая.");
    }

    @Test
    void testRemoveFromHistoryShouldNotReturnException() {

        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История  пустая.");
        assertEquals(1, history.size(), "История пустая.");

        inMemoryHistoryManager.remove(task.getId());

        assertEquals(0, inMemoryHistoryManager.getHistory().size(), "История не пустая.");


    }

    @Test
    void TestHistoryAfterViewingOneTask() {
        inMemoryHistoryManager.remove(task.getId());
        Task task2 = new Task("new name3", "new description2");
        taskManager.createTask(task2);
        inMemoryHistoryManager.printHistoryLinks();

        taskManager.getStandaloneTask(100);
        assertTrue(inMemoryHistoryManager.getHistory().contains(task));
    }


    @Test
    void TestHistoryAfterViewingTwoTask() {
        inMemoryHistoryManager.remove(task.getId());

        taskManager.createTask(task);

        Task task2 = new Task("new name", "new description");

        taskManager.createTask(task2);

        taskManager.getStandaloneTask(100);
        assertTrue(inMemoryHistoryManager.getHistory().contains(task));
    }


}
