import java.util.UUID;

/**
 * @author A.Gabov
 */
public class SubTask extends Task {

    private UUID epicId;

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
        return "{ " + getName() + ", " + (getDescription() != null ? getDescription():"'empty description'") + "," + getStatusName(getStatus()) + "}";
    }

}
