package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * @author A.Gabov
 */
public class Task {
    private String name;
    private String description;
    private Integer id;
    private String status;
    private Optional<LocalDateTime> startTime;

    private Optional<Integer> duration;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Optional.ofNullable(startTime);
    }


    public static final DateTimeFormatter getFormatter() {
        return formatter;
    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(Task anotherTask) {
        this.name = anotherTask.name;
        this.description = anotherTask.description;
        this.status = anotherTask.status;
        this.startTime = anotherTask.startTime;
        this.duration = anotherTask.duration;
        this.id = anotherTask.id;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.startTime = Optional.empty();
        this.duration = Optional.empty();
        this.status = TaskStatus.NEW.toString();
    }

    public Task(String name, String description, String status, LocalDateTime startTime, Integer duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = Optional.ofNullable(startTime);
        this.duration = Optional.ofNullable(duration);
    }

    public Task(String name, String description, String status, int id, LocalDateTime startTime, Integer duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = Optional.ofNullable(startTime);
        this.duration = Optional.ofNullable(duration);
    }

    public Optional<Integer> getDuration() {
        return duration;
    }

    public void setDuration(Optional<Integer> duration) {
        this.duration = duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public Optional<LocalDateTime> getEndTime() {

        return (startTime.isPresent() && duration.isPresent()) ?
                Optional.ofNullable(this.startTime.get().plus(Duration.ofMinutes(this.duration.get()))) :
                Optional.empty();

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
        sb.append(getId() != null ? getId().toString() : "' '").append(",");
        sb.append(TaskType.TASK).append(",");
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
