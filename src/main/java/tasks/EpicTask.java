package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author A.Gabov
 */
public class EpicTask extends Task {

    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private LocalDateTime endTime;

    public EpicTask(String name, String description, String status, LocalDateTime startTime, Integer duration) {
        super(name, description, status, startTime, duration);
    }

    public EpicTask(String name, String description, String status, int id, LocalDateTime startTime, Integer duration) {
        super(name, description, status, id, startTime, duration);
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
        sb.append(getId().toString()).append(",");
        sb.append(TaskType.EPIC).append(",");
        sb.append(getName()).append(",");
        sb.append(getStatus()).append(",");
        sb.append(getDescription() != null ? getDescription() : "' '").append(",");
        sb.append(getStartTime().format(formatter)).append(",");
        sb.append(getDuration() != null ? getDuration() : "' '");
        return sb.toString();
    }
}
