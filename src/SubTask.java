import java.util.UUID;

/**
 * @author A.Gabov
 */
public class SubTask extends Task {

    private UUID epicId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name, String description, String status) {
        super(name, description, status);
    }
    public SubTask(String name, String description, String status, UUID epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public UUID getEpicId() {
        return epicId;
    }

    public void setEpicId(UUID epicId) {
        if (epicId != null) {
            this.epicId = epicId;
        } else {
            throw new NullPointerException("epic ID cannot be null");
        }
    }

    @Override
    public String toString() {
        return "{ " + getName() + ", " + (getDescription() != null ? getDescription():"'empty description'") + "," + getStatus() + "}";
    }

}
