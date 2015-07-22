package edu.fordham.wisdm.gaitdetector;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

/**
 * Custom adapter that implements layout defined in activity_list_layout.xml.
 */
public class TaskAdapter extends ArrayAdapter<Task>{

    /**
     * Reference of the activity.
     */
    Context context;

    /**
     * Resource id of layout file. In this case, activity_list_layout.xml.
     */
    int layoutResourceId;

    /**
     * Array of Task objects to be attached to View by our custom adapter.
     */
    Task data[] = null;

    /**
     * Constructor.
     * @param context
     * @param layoutResourceId
     * @param data
     */
    public TaskAdapter(Context context, int layoutResourceId, Task[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    /**
     * Overrides getView method, which is called for every item to be in ListView.
     * @param position
     * @param convertView
     * @param parent
     * @return row
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TaskHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            /**
             * Temporary object to cache TextView & ImageView.
             */
            holder = new TaskHolder();
            holder.completionIcon = (ImageView)row.findViewById(R.id.layout_completion_icon);
            holder.taskID = (TextView)row.findViewById(R.id.layout_activity_title);
            holder.completionDate = (TextView)row.findViewById(R.id.layout_completion_date);

            row.setTag(holder);
        }
        else
        {
            holder = (TaskHolder)row.getTag();
        }

        Task task = data[position];
        holder.taskID.setText(task.taskID);
        holder.completionDate.setText(task.completionDate);
        holder.completionIcon.setImageResource(task.completionIcon);

        return row;
    }

    /**
     * Temporary holder class used by getView function. Used to improve performance as it caches
     * the TextView & ImageView.
     */
    static class TaskHolder {
        TextView taskID;
        TextView completionDate;
        ImageView completionIcon;
    }
}