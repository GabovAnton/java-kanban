package tasks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author A.Gabov
 */
public class Task {
    private String name;
    private String description;
    private Integer id;
    private String status;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
    public Task(String name, String description, String status, int id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime(){
        return this.startTime.plus(duration);
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
        return getId().toString() + "," + TaskType.TASK + "," + getName() + "," + duration +  getStatus() + "," + (getDescription() != null ? getDescription() : "' '");
    }

}
