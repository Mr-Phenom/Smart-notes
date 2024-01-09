package com.company.smartnotes.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.smartnotes.R;
import com.company.smartnotes.Room.RoomReminder;
import com.company.smartnotes.Room.RoomSimpleNotes;

import java.util.ArrayList;
import java.util.List;

public class AdapterClassReminder extends RecyclerView.Adapter<AdapterClassReminder.ReminderCardViewHolder> {

    private List<RoomReminder> reminders = new ArrayList<>();

    OnLongItemClickListener longItemClickListener;
    onItemClickListener itemClickListener;



    @NonNull
    @Override
    public ReminderCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design_reminder,parent,false);

        return new ReminderCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderCardViewHolder holder, int position) {

        RoomReminder currReminder = reminders.get(position);
        holder.title.setText(currReminder.getTitle().toString());
        holder.description.setText(currReminder.getDescription().toString());
        holder.time.setText(currReminder.getTime().toString());

        if(currReminder.isNotify())
        {
            holder.notify.setText("Notification");
            holder.notify.setTextColor(Color.GREEN);
        }
        else
        {
            holder.notify.setText("Notification");
            holder.notify.setTextColor(Color.RED);
        }

        if(currReminder.isAlarm())
        {
            holder.alarm.setText("Alarm");
            holder.alarm.setTextColor(Color.GREEN);
        }
        else
        {
            holder.alarm.setText("Alarm");
            holder.alarm.setTextColor(Color.RED);
        }

        if(currReminder.getSelectTime()<=System.currentTimeMillis())
        {
            holder.time.setText("DONE");
            holder.layout.setBackgroundColor(Color.parseColor("#FFAFB1BB"));

        }
        else
        {
            holder.layout.setBackgroundColor(Color.parseColor("#BD98FE"));
        }



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(longItemClickListener!=null)
                {
                    longItemClickListener.itemLongClicked(v,position);
                }
                return false;
            }
        });
    }

    public interface OnLongItemClickListener{
        void itemLongClicked(View v,int position);
    }
    public void setOnLongItemClickListener(OnLongItemClickListener longClickListener)
    {
        this.longItemClickListener=longClickListener;
    }

    public RoomReminder getReminder(int position)
    {
        return reminders.get(position);
    }


    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void setReminders(List<RoomReminder> reminders)
    {
        this.reminders=reminders;
        notifyDataSetChanged();
    }

    public interface onItemClickListener
    {
        void onItemClick(RoomReminder reminder);
    }

    public void setOnItemClickListener(AdapterClassReminder.onItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public class ReminderCardViewHolder extends RecyclerView.ViewHolder{

        TextView title,description,time,notify,alarm;
        LinearLayout layout;
        public ReminderCardViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textViewTitleCardDesignReminder);
            description = itemView.findViewById(R.id.textViewDescriptionCardDesignReminder);
            time = itemView.findViewById(R.id.textViewTimeCardDesignReminder);
            notify = itemView.findViewById(R.id.textViewNotificationStatusCardDesignReminder);
            alarm = itemView.findViewById(R.id.textViewAlarmStatusCardDesignReminder);

            layout = itemView.findViewById(R.id.linearLayourCardReminder);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if(itemClickListener!=null && position!=RecyclerView.NO_POSITION)
                    {
                        itemClickListener.onItemClick(reminders.get(position));
                    }
                }
            });
        }
    }
}


