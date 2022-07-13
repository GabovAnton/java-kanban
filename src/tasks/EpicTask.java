package tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private ArrayList<Integer> subTasks = new ArrayList<>();

    public EpicTask(String name, String description, String status) {
        super(name, description, status);
    }

    public EpicTask(String name, String description) {
        super(name, description);
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
        return "Tasks.EpicTask: " + getName() + ", " + (getDescription() != null ? getDescription() : "'empty description'") + ", ID: " + getId().toString() + ", " + getStatus() + ", SubTasksIDs: " + getSubTasks();
    }
}
