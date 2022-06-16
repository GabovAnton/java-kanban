package TaskManagers;

import Tasks.EpicTask;
import Tasks.SubTask;
import Tasks.Task;

import java.util.UUID;

/**
 * @author A.Gabov
 */
public interface PublicInterface {
    Task getStandaloneTask(UUID uid);

    SubTask getSubtask(UUID uid);

    EpicTask getEpic(UUID uid);
}
