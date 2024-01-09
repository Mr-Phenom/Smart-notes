package com.company.smartnotes.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.company.smartnotes.Activities.NewReminderActivity;
import com.company.smartnotes.Activities.UpdateNoteActivity;
import com.company.smartnotes.Activities.UpdateReminderActivity;
import com.company.smartnotes.Adapters.AdapterClassReminder;
import com.company.smartnotes.Adapters.AdapterClassSimpleNote;
import com.company.smartnotes.ModelClass.ReminderViewModel;
import com.company.smartnotes.R;
import com.company.smartnotes.Reciever.RecieverForAlarm;
import com.company.smartnotes.Reciever.RecieverForReminderNotification;
import com.company.smartnotes.Reciever.RecieverForStopNotification;
import com.company.smartnotes.Room.RoomReminder;
import com.company.smartnotes.Room.RoomSimpleNotes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ReminderFragment extends Fragment {

    private static final String PERMISSION_NOTIFICATION = Manifest.permission.POST_NOTIFICATIONS;
    RecyclerView recyclerViewReminder;
    FloatingActionButton fab;
    AdapterClassReminder adapter;
    ReminderViewModel reminderViewModel;
    List<RoomReminder> roomReminderList;

    ActivityResultLauncher<Intent> activityResultLauncherForAddReminder;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateReminder;
    private int currPosition;

    public static ReminderFragment getInstance()
    {
        return new ReminderFragment();
    }



    public ReminderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);

        recyclerViewReminder = v.findViewById(R.id.recyclerViewReminder);
        fab = v.findViewById(R.id.floatingActionButtonReminder);
        recyclerViewReminder.setLayoutManager(new GridLayoutManager(getActivity(),2));
        adapter = new AdapterClassReminder();
        recyclerViewReminder.setAdapter(adapter);


        registerActivityForAddReminder();
        registerActivityForUpdateReminder();

        reminderViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(ReminderViewModel.class);
        reminderViewModel.getAllReminders().observe(getActivity(), new Observer<List<RoomReminder>>() {
            @Override
            public void onChanged(List<RoomReminder> roomReminders) {
                adapter.setReminders(roomReminders);
                Log.d("SimpleReminderFragment", "Data changed: " + roomReminders.size() + " items");

                if(roomReminders.size()!=0)
                {
                    RoomReminder latestReminder = roomReminders.get(roomReminders.size()-1);
                    int id = latestReminder.getId();
                    Log.d("SimpleRem", String.valueOf(id));
                    if(latestReminder.getSelectTime()> System.currentTimeMillis())
                    {
                        alarmManagerBuild(id,latestReminder.getTitle(),latestReminder.getDescription(),latestReminder.getTime(),latestReminder.getNotificationTime(),latestReminder.getSelectTime(),latestReminder.isNotify(),latestReminder.isAlarm());
                    }

                }


            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestRuntimePermission();
                //startActivity(i);
            }
        });

        adapter.setOnLongItemClickListener(new AdapterClassReminder.OnLongItemClickListener() {
            @Override
            public void itemLongClicked(View v, int position) {
                currPosition=position;
            }
        });

        adapter.setOnItemClickListener(new AdapterClassReminder.onItemClickListener() {
            @Override
            public void onItemClick(RoomReminder reminder) {
                Intent intent = new Intent(getActivity(), UpdateReminderActivity.class);
                intent.putExtra("idForUpdateReminder",reminder.getId());
                intent.putExtra("titleForUpdateReminder",reminder.getTitle().toString());
                intent.putExtra("descriptionForUpdateReminder",reminder.getDescription().toString());
                intent.putExtra("timeForUpdateReminder",reminder.getTime().toString());
                intent.putExtra("notificationTimeForUpdateReminder",reminder.getNotificationTime());
                intent.putExtra("selectTimeForUpdateReminder",reminder.getSelectTime());
                intent.putExtra("isNotifyForUpdateReminder",reminder.isNotify());
                intent.putExtra("isAlarmForUpdateReminder",reminder.isAlarm());
                activityResultLauncherForUpdateReminder.launch(intent);

            }
        });

        registerForContextMenu(recyclerViewReminder);


        return v;
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.reminder_delete_menu,menu);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        if(item.getItemId()==R.id.reminder_delete)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete").setMessage("Are you sure you want to delete?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RoomReminder r = adapter.getReminder(currPosition);
                            if(r.isAlarm() && (r.getSelectTime()>System.currentTimeMillis()))
                            {
                                Intent cancelIntent = new Intent(getActivity(), RecieverForAlarm.class); // Use the same receiver class
                                PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getActivity(),r.getId()+10000,cancelIntent,PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(cancelPendingIntent);
                            }
                            if(r.isNotify() && (r.getNotificationTime()>System.currentTimeMillis()))
                            {
                                Intent cancelIntent = new Intent(getActivity(), RecieverForReminderNotification.class); // Use the same receiver class
                                PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getActivity(),r.getId(),cancelIntent,PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(cancelPendingIntent);
                            }
                            if(r.isNotify() && (r.getNotificationTime()<System.currentTimeMillis()) && (r.getSelectTime()>System.currentTimeMillis()))
                            {
                                Intent iBroadCase3 = new Intent(getActivity(), RecieverForStopNotification.class);
                                iBroadCase3.putExtra("idForStopNotification",r.getId());
                                PendingIntent stopNotificationIntent = PendingIntent.getBroadcast(getActivity(),r.getId(),iBroadCase3,PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager alarmManager1 = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                alarmManager1.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),stopNotificationIntent);
                            }
                            reminderViewModel.delete(adapter.getReminder(currPosition));

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();


        }

        return super.onContextItemSelected(item);
    }




    public void registerActivityForAddReminder()
    {
        activityResultLauncherForAddReminder = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        int resultCode = o.getResultCode();
                        Intent data = o.getData();
                        if(resultCode == RESULT_OK && data!=null)
                        {
                            String title = data.getStringExtra("titleNewReminder");
                            String description = data.getStringExtra("descriptionNewReminder");
                            String time = data.getStringExtra("timeNewReminder");
                            long notificationTime = data.getLongExtra("notificationTimeNewReminder",00);
                            long selectTime = data.getLongExtra("selectTimeNewReminder",00);
                            boolean isNotify = data.getBooleanExtra("isNotifyNewReminder",false);
                            boolean isAlarm = data.getBooleanExtra("isAlarmNewReminder",false);

                           RoomReminder reminder = new RoomReminder(title,description,time,notificationTime,selectTime,isNotify,isAlarm);
                           reminderViewModel.insert(reminder);



                        }
                    }
                });
  }


    public void registerActivityForUpdateReminder()
    {
        activityResultLauncherForUpdateReminder = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        int resultCode = o.getResultCode();
                        Intent data = o.getData();
                        if(resultCode==RESULT_OK && data!=null)
                        {
                           int id = data.getIntExtra("idFromUpdateReminder",-1);
                           String getTitle = data.getStringExtra("titleFromUpdateReminder");
                            String getDescription = data.getStringExtra("descriptionFromUpdateReminder");
                            String getDate =  data.getStringExtra("timeFromUpdateReminder");
                            long notificationTIme = data.getLongExtra("notificationTimeFromUpdateReminder",0);
                            long selectTime = data.getLongExtra("selectTimeFromUpdateReminder",0);
                            boolean isNotify = data.getBooleanExtra("isNotifyFromUpdateReminder",false);
                            boolean isAlarm = data.getBooleanExtra("isAlarmFromUpdateReminder",false);

                            alarmManagerBuild(id,getTitle,getDescription,getDate,notificationTIme,selectTime,isNotify,isAlarm);
                            if(!isNotify)
                            {
                                Intent cancelIntent = new Intent(getActivity(), RecieverForReminderNotification.class); // Use the same receiver class
                                PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getActivity(),id,cancelIntent,PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(cancelPendingIntent);
                            }
                            if(!isAlarm)
                            {
                                Intent cancelIntent = new Intent(getActivity(), RecieverForAlarm.class); // Use the same receiver class
                                PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getActivity(),id+10000,cancelIntent,PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(cancelPendingIntent);
                            }

                            RoomReminder r = new RoomReminder(getTitle,getDescription,getDate,notificationTIme,selectTime,isNotify,isAlarm);
                            r.setId(id);
                            reminderViewModel.update(r);
                        }

                    }
                });
    }


    public void alarmManagerBuild(int id,String getTitle,String getDescription,String getDate,long triggerTime,long selectTime,boolean isNotify,boolean isAlarm)
    {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        Intent iBroadcast = new Intent(getActivity(), RecieverForReminderNotification.class);
        Intent iBroadcast2 = new Intent(getActivity(), RecieverForAlarm.class);


        iBroadcast.putExtra("titleForReciever",getTitle);
        iBroadcast.putExtra("descriptionForReciever",getDescription);
        iBroadcast.putExtra("timeForReciever",getDate);
        iBroadcast.putExtra("idForReciever",id);

        iBroadcast2.putExtra("titleForReciever2",getTitle);
        iBroadcast2.putExtra("descriptionForReciever2",getDescription);
        iBroadcast2.putExtra("timeForReciever2",getDate);
        iBroadcast2.putExtra("idForReciever2",id+10000);

        if(isNotify)
        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),id,iBroadcast,PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,triggerTime,pendingIntent);

            Intent iBroadCase3 = new Intent(getActivity(), RecieverForStopNotification.class);
            iBroadCase3.putExtra("idForStopNotification",id);
            PendingIntent stopNotificationIntent = PendingIntent.getBroadcast(getActivity(),id,iBroadCase3,PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager1 = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager1.set(AlarmManager.RTC_WAKEUP,selectTime,stopNotificationIntent);
        }

        if(isAlarm)
        {
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getActivity(),id+10000,iBroadcast2,PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,selectTime,pendingIntent2);
        }

    }

   private void requestRuntimePermission()
    {
        if(ActivityCompat.checkSelfPermission(getActivity(),PERMISSION_NOTIFICATION)== PackageManager.PERMISSION_GRANTED)
        {
            Intent i = new Intent(getActivity(), NewReminderActivity.class);
            activityResultLauncherForAddReminder.launch(i);
        }
        else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),PERMISSION_NOTIFICATION))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("For setting Reminder NOTIFICATION PERMISSION is required")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{PERMISSION_NOTIFICATION},999);
                            dialog.dismiss();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{PERMISSION_NOTIFICATION},999);
            Toast.makeText(getActivity(), "Here it is", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if(requestCode==999)
//        {
//            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
//            {
//                Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(getActivity(), NewReminderActivity.class);
//                activityResultLauncherForAddReminder.launch(i);
//            }
//            else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),PERMISSION_NOTIFICATION))
//            {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage("Notification Permission needed for set reminders. Please allow Notification Permission from Settings")
//                        .setTitle("Permission Required")
//                        .setCancelable(false)
//                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",getActivity().getPackageName(),null);
//                                i.setData(uri);
//                                startActivity(i);
//                                dialog.dismiss();
//                            }
//                        });
//                builder.show();
//            }
//            else
//            {
//                requestRuntimePermission();
//            }
//        }
//    }
}