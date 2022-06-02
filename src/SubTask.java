import java.util.UUID;

/**
 * @author A.Gabov
 */
public class SubTask extends Task {

    private UUID epicTaskUUID;

    public UUID getEpicTaskUUID() {
        return epicTaskUUID;
    }

    public void setEpicTaskUUID(UUID epicTaskUUID) {
        this.epicTaskUUID = epicTaskUUID;
    }


}
