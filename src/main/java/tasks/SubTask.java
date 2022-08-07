package tasks;


/**
 * @author A.Gabov
 */
public class SubTask extends Task {

    private Integer epicId;

    public SubTask(String name, String description, String status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
    public SubTask(String name, String description, String status, Integer epicId, int id) {
        super(name, description, status, id);
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
        return getId().toString() + "," + TaskType.SUBTASK + "," + getName() + "," +   getStatus() + "," + (getDescription() != null ? getDescription() : "' '") + "," + getEpicId();
    }

}
