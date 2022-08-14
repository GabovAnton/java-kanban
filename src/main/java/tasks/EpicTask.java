package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private ArrayList<Integer> subTasks = new ArrayList<>();

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private LocalDateTime endTime;

    public EpicTask(String name, String description, String status) {
        super(name, description, status, null, null);
    }

    public EpicTask(EpicTask anotherTask) {
        super(anotherTask);
        this.subTasks = anotherTask.subTasks;
        this.endTime = anotherTask.endTime;

    }

    public EpicTask(String name, String description, String status, int id) {
        super(name, description, status, id, null, null);
    }


    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTasks(Integer SubTaskId) {
        if (!this.subTasks.contains(SubTaskId)) {
            this.subTasks.add(SubTaskId);
        }
    }

    public void removeAllSubtasks() {
        this.subTasks.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getId() != null ? getId().toString() : "' '").append(",");
        sb.append(TaskType.EPIC).append(",");
        sb.append(getName()).append(",");
        sb.append(getStatus()).append(",");
        sb.append(getDescription() != null ? getDescription() : "' '").append(",");
        sb.append(getStartTime() != null ? getStartTime().isPresent() ? getStartTime().get().format(formatter) : "' '" : "' '").append(",");
        ;
        sb.append(getDuration() != null ? getDuration().isPresent() ? getDuration().get() : "' '" : "' '").append(",");
        ;
        return sb.toString();
    }
}
