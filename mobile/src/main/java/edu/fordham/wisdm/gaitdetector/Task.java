package edu.fordham.wisdm.gaitdetector;

/**
 * Class of task objects containing title, date completed and completion icon.
 */
public class Task {

    /**
     * Name of task.
     */
    public String taskID;

    /**
     * Date & time of completion. If incomplete, N/A.
     */
    public String completionDate;

    /**
     * Completion icon - check or X.
     */
    public int completionIcon;

    /**
     * Constructors.
     */
    public Task() {
        super();
    }

    public Task(String taskID, String completionDate, int completionIcon) {
        super();
        this.taskID = taskID;
        this.completionDate = completionDate;
        this.completionIcon = completionIcon;
    }

    /**
     * toString Override to access the taskID
     * @return
     */
    @Override
    public String toString(){
        return this.taskID;
    }
}