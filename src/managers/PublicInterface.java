package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;


/**
 * @author A.Gabov
 */
public interface PublicInterface {
    Task getStandaloneTask(Integer id);

    SubTask getSubtask(Integer id);

    EpicTask getEpic(Integer id);
}
