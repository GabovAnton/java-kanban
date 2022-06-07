import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private ArrayList<SubTask> subTasks = new ArrayList<>();

    public EpicTask(String name, String description, String status) {
        super(name, description, status);
    }
    public EpicTask(String name, String description) {
        super(name, description);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTasks(SubTask task) {
        if (!this.subTasks.contains(task)) {
            this.subTasks.add(task);
        }
    }

    @Override
    public String toString() {
        return "EpicTask: " +getName() + ", " + (getDescription() != null ? getDescription():"'empty description'") + ", ID: " + getId().toString() + ", " + getStatus() + ",  Subtasks: " + getSubTasks(); // просто возвращаем поля класса
    }
}
