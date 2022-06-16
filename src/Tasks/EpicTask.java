package Tasks;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private ArrayList<UUID> subTasks = new ArrayList<>();

    public EpicTask(String name, String description, String status) {
        super(name, description, status);
    }
    public EpicTask(String name, String description) {
        super(name, description);
    }

    public List<UUID> getSubTasks() {
        return subTasks;
    }

    public void addSubTasks(UUID SubTaskId) {
        if (!this.subTasks.contains(SubTaskId)) {
            this.subTasks.add(SubTaskId);
        }
    }

    @Override
    public String toString() {
        return "Tasks.EpicTask: " +getName() + ", " + (getDescription() != null ? getDescription():"'empty description'") + ", ID: " + getId().toString() + ", " + getStatus() + ", SubTasksIDs: " + getSubTasks() ;
    }
}
