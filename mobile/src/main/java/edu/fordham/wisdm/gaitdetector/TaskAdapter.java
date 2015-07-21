package edu.fordham.wisdm.gaitdetector;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

public class TaskAdapter extends ArrayAdapter<Task>{

    Context context;
    int layoutResourceId;
    Task data[] = null;

    public TaskAdapter(Context context, int layoutResourceId, Task[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TaskHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TaskHolder();
            holder.completionIcon = (ImageView)row.findViewById(R.id.completion_icon);
            holder.activityTitle = (TextView)row.findViewById(R.id.activity_title);
            holder.completionDate = (TextView)row.findViewById(R.id.completion_date);

            row.setTag(holder);
        }
        else
        {
            holder = (TaskHolder)row.getTag();
        }

        Task task = data[position];
        holder.activityTitle.setText(task.activityTitle);
        holder.completionDate.setText(task.completionDate);
        holder.completionIcon.setImageResource(task.completionIcon);

        return row;
    }

    static class TaskHolder {
        ImageView completionIcon;
        TextView activityTitle;
        TextView completionDate;
    }
}
