package edu.fordham.wisdm.gaitdetector;

/**
 * Class of task objects containing title, date completed and completion icon.
 */
public class Task {
    public int completionIcon;
    public String activityTitle;
    public String completionDate;

    public Task(){
        super();
    }

    public Task (int completionIcon, String activityTitle, String completionDate){
        super();
        this.completionIcon = completionIcon;
        this.activityTitle = activityTitle;
        this.completionDate = completionDate;
    }
}