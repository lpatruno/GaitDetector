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
    public Task(String taskTitle, String completionDate, int completionIcon) {
        this.taskTitle = taskTitle;
        this.completionDate = completionDate;
        this.completionIcon = completionIcon;
    }

    /**
     * toString Override to access the taskTitle
     * @return
     */
    @Override
    public String toString(){
        return this.taskTitle;
    }
}