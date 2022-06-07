import java.util.Objects;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class Task {
    private String name;
    private String description;
    private UUID id;
    private String status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = UUID.randomUUID();
    }

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = UUID.randomUUID();
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

    public UUID getId() {
        return id;
    }

    public void setId() {
        this.id = UUID.randomUUID();
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
        SubTask otherTask = (SubTask) obj;
        return Objects.equals(getId(), otherTask.getId()) &&
                Objects.equals(getName(), otherTask.getName()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                Objects.equals(getStatus(), otherTask.getStatus());
    }

    @Override
    public String toString() {
        return "Task: " + getName() + ", " + (getDescription() != null ? getDescription() : "'empty description'") + ", ID: " + getId().toString() + ", " + getStatus();
    }

}
