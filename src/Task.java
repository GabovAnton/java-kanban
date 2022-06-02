import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author A.Gabov
 */
public class Task {
    private String name;
    private String description;
    private UUID uuid;
    private Integer status;

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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid() {
        this.uuid = UUID.randomUUID();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    @Override
    public int hashCode() {
        int hash = 17;
        if (getUuid() != null) {
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
        return Objects.equals(getUuid(), otherTask.getUuid()) &&
                Objects.equals(getName(), otherTask.getName()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                Objects.equals(getStatus(), otherTask.getStatus());
    }

    public String getStatusName(Integer status) {
        switch (status) {
            case (1):
                return "NEW";
            case (2):
                return "IN_PROGRESS";
            case (3):
                return "DONE";
            default:
                return "Error, while parsing statusName"; //handling error
        }
    }

}
