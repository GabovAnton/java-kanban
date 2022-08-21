package managers;

/**
 * @author A.Gabov
 */
public class Managers {

    static final TaskManager HTTPTaskManager = new HTTPTaskManager("http://localhost:8078");

    public static TaskManager getDefault() {
        return HTTPTaskManager;
    }

    public static TaskManager getHTTPTaskManager() {
        return HTTPTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
