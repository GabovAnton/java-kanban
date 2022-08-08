package tasks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author A.Gabov
 */
public class Task {
    private String name;
    private String description;
    private Integer id;
    private String status;



    private Integer duration;
    private LocalDateTime startTime;

    public static final DateTimeFormatter getFormatter() {
        return formatter;
    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, String status, LocalDateTime startTime, Integer duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }
    public Task(String name, String description, String status, int id, LocalDateTime startTime, Integer duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime(){
        return this.startTime.plus(Duration.ofMinutes(this.duration));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (getId() != null) {
            hash = hash + getName().hashCode();
        }
        hash = hash * 31;

        if (getDescription() != null) {
            hash = hash + getDescription().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        Task otherTask = (Task) obj;
        return Objects.equals(getId(), otherTask.getId()) &&
                Objects.equals(getName(), otherTask.getName()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                Objects.equals(getStatus(), otherTask.getStatus());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getId().toString()).append(",");
        sb.append(TaskType.TASK).append(",");
        sb.append(getName()).append(",");
        sb.append(getStatus()).append(",");
        sb.append(getDescription()!= null ? getDescription() : "' '").append(",");
        sb.append(getStartTime().format(formatter)).append(",");
        sb.append(getDuration()!= null ?  getDuration() : "' '");
        return sb.toString();

      /*  return getId().toString() + "," + TaskType.TASK + "," + getName() + ","  +  getStatus() + "," +
                (getDescription()!= null ? getDescription() : "' '") + "," +
                (getStartTime().format(formatter)!= null ? getStartTime().format(formatter) : "' '") + "," +
                ( getDuration()!= null ?  getDuration() : "' '");*/

    }

}
