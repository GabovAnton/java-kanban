package managers;

/**
 * @author A.Gabov
 */
public class Managers {
    protected static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public static  TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}
