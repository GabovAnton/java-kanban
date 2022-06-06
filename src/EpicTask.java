import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private List<SubTask> subTasks = new ArrayList<>();

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
        return "EpicTask: " +getName() + ", " + (getDescription() != null ? getDescription():"'empty description'") + ", ID: " + getId().toString() + ", " + getStatusName(getStatus()) + ",  Subtasks: " + getSubTasks(); // просто возвращаем поля класса
    }
}
