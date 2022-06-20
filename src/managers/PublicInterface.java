package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.UUID;

/**
 * @author A.Gabov
 */
public interface PublicInterface {
    Task getStandaloneTask(UUID id);

    SubTask getSubtask(UUID id);

    EpicTask getEpic(UUID id);
}
