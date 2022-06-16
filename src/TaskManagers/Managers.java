package TaskManagers;

import HistoryManager.HistoryManager;
import HistoryManager.InMemoryHistoryManager;

/**
 * @author A.Gabov
 */
public class Managers {
    protected   static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public static <T extends TaskManager> T getDefault() {
        return (T) new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}
