package tasks;


/**
 * @author A.Gabov
 */
public class SubTask extends Task {

    private Integer epicId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name, String description, String status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, String status, Integer epicId) {
        super(name, description, status);
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
        return "        Subtask {" + getName() + ", " + (getDescription() != null ? getDescription() : "'empty description'") + ", ID: " + getId() + "," + getStatus() + "}";
    }

}
