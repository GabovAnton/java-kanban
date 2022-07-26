package tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public EpicTask(String name, String description, String status) {
        super(name, description, status);
    }
    public EpicTask(String name, String description, String status, int id) {
        super(name, description, status, id);
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTasks(Integer SubTaskId) {
        if (!this.subTasks.contains(SubTaskId)) {
            this.subTasks.add(SubTaskId);
        }
    }

    @Override
    public String toString() {
        return getId().toString() + "," + TaskType.EPIC + "," + getName() + "," +   getStatus() + "," + (getDescription() != null ? getDescription() : "' '");    }
}
