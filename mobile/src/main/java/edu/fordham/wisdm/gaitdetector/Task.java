package edu.fordham.wisdm.gaitdetector;

/**
 * Class of task objects containing title, date completed and completion icon.
 */
public class Task {

    /**
     * Name of task.
     */
    public String taskTitle;

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

    public Task(String taskTitle, String completionDate, int completionIcon) {
        super();
        this.taskTitle = taskTitle;
        this.completionDate = completionDate;
        this.completionIcon = completionIcon;
    }
}