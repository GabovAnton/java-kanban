package tasks;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author A.Gabov
 */
public class SubTask extends Task {

    private Integer epicId;

      public SubTask(String name, String description, String status, Integer epicId,LocalDateTime startTime, Integer duration) {
        super(name, description, status,startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, String status, Integer epicId, int id, LocalDateTime startTime, Integer duration) {
        super(name, description, status, id, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId != null) {
            this.epicId = epicId;
        } else {
            throw new NullPointerException("epic ID cannot be null");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getId().toString()).append(",");
        sb.append(TaskType.SUBTASK).append(",");
        sb.append(getName()).append(",");
        sb.append(getStatus()).append(",");
        sb.append(getDescription() != null ? getDescription() : "' '").append(",");
        sb.append(getStartTime().format(formatter)).append(",");
        sb.append(getDuration() != null ? getDuration() : "' '").append(",");
        sb.append(getEpicId());
        return sb.toString();
    }

}
