package com.company.smartnotes.Activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.company.smartnotes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewReminderActivity extends AppCompatActivity {
    EditText title,description;
    TextView notificationStatus,alarmStatus;
    Button timePicker;
    String getTitle,getDescription;
    String getDate;
    int currDay,currMonth,currYear,selectDay=-1,selectMonth=-1,selectYear=-1,selectHour=-1,selectMinute=-1;
    int hours;
    Boolean isNotify,isAlarm;
    long notificationTIme,selectTime;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        title = findViewById(R.id.editTextTitleNewReminder);
        description = findViewById(R.id.editTextDescriptionNewReminder);
        timePicker = findViewById(R.id.buttonTimePickerNewReminder);
        notificationStatus=findViewById(R.id.textViewNotificationStatus);
        alarmStatus = findViewById(R.id.textViewAlarmStatus);
        Toolbar toolbar = findViewById(R.id.toolbar_new_reminder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Reminder");



//        getTitle = title.getText().toString();
//        getDescription= description.getText().toString();

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // currDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Calendar calendar = Calendar.getInstance();
                currDay=calendar.get(Calendar.DAY_OF_MONTH);
                currMonth=calendar.get(Calendar.MONTH);
                currYear=calendar.get(Calendar.YEAR);

                dateDialogue();

            }
        });

        setOnBackPress();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_menu_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.top_menu_save)
        {
            Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
            getTitle = title.getText().toString();
            getDescription = description.getText().toString();
            getDate = selectYear+"/"+selectMonth+"/"+selectDay+" "+selectHour+":"+selectMinute;
            if(getTitle.equals("") || getDescription.equals(""))
            {
                Toast.makeText(NewReminderActivity.this, "Title and description can not be Empty", Toast.LENGTH_SHORT).show();
            }
            else if(selectDay==-1 || selectHour==-1 || selectMonth==-1 || selectYear==-1 || selectMinute ==-1)
            {
                Toast.makeText(this, "You must select time!", Toast.LENGTH_SHORT).show();
            }
            else
            {

                String selectedDate = selectYear+"/"+selectMonth+"/"+selectDay+" "+selectHour+":"+selectMinute+":00";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    Date date = sdf.parse(selectedDate);
                    selectTime = date.getTime();
                    notificationTIme = date.getTime() - hours*60*60*1000;

                   // alarmManagerBuild(millis, date.getTime());
                } catch (ParseException e) {
                    Toast.makeText(this, "Uhu exception", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }

                Intent intent = new Intent();
                intent.putExtra("titleNewReminder",getTitle);
                intent.putExtra("descriptionNewReminder",getDescription);
                intent.putExtra("timeNewReminder",getDate);
                intent.putExtra("selectTimeNewReminder",selectTime);
                intent.putExtra("notificationTimeNewReminder",notificationTIme);
                intent.putExtra("isNotifyNewReminder",isNotify);
                intent.putExtra("isAlarmNewReminder",isAlarm);
                setResult(RESULT_OK,intent);
                finish();



            }
        }
        return false;
    }

    public void dateDialogue()
    {
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectDay=dayOfMonth;
                selectMonth=month+1;
                selectYear=year;
                timeDialogue();
            }
        }, currYear, currMonth, currDay);
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE,"Ok",datePicker);
        //datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE,"Cancel",datePicker);
        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(NewReminderActivity.this, "You must set a date for your event.", Toast.LENGTH_LONG).show();
            }
        });
        datePicker.show();
    }

    public void timeDialogue()
    {
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int convertedHour;
                selectHour=hourOfDay;
                selectMinute=minute;
                if(selectHour>12)
                {
                    convertedHour = selectHour%12;
                    if(convertedHour==0)
                    {
                        convertedHour=12;
                    }
                    getDate = (selectDay + "/" + selectMonth + "/" + selectYear + " at "+ selectHour + " : " + selectMinute+" PM");
                }
                else
                {
                    convertedHour=selectHour;
                    if(convertedHour==0)
                        convertedHour=12;
                    getDate = (selectDay + "/" + selectMonth + "/" + selectYear + " at "+ selectHour + " : " + selectMinute+" AM");
                }

                settingsDialogue();

            }
        },12,00,false);
        timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(NewReminderActivity.this, "You must set a time", Toast.LENGTH_SHORT).show();
            }
        });
        timePicker.show();
    }

    public void settingsDialogue()
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reminder_settings);
        dialog.setCanceledOnTouchOutside(false);
        CheckBox checkBoxNotification,checkBoxAlarm;
        EditText editTextHours;
        editTextHours=dialog.findViewById(R.id.editTextNotifitionNumber);
        checkBoxNotification=dialog.findViewById(R.id.checkBoxNotification);
        checkBoxAlarm=dialog.findViewById(R.id.checkBoxAlarm);



        checkBoxNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxNotification.isChecked())
                {
                    editTextHours.setClickable(true);

                    // editTextHours.setBackground(Drawable.createFromPath("@drawable/edittext_border"));
                    checkBoxNotification.setTextColor(Color.BLACK);
                }
                else
                {
                    editTextHours.setClickable(false);
                    //editTextHours.setBackground(new ColorDrawable(Color.RED));
                    checkBoxNotification.setTextColor(Color.RED);

                }
            }
        });

        checkBoxAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxAlarm.isChecked())
                {
                    checkBoxAlarm.setTextColor(Color.BLACK);
                }
                else
                {
                    checkBoxAlarm.setTextColor(Color.RED);
                }
            }
        });

        Button btn = dialog.findViewById(R.id.buttonNotificationDialog);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAlarm=checkBoxAlarm.isChecked();
                isNotify=checkBoxNotification.isChecked();

                if (!isNotify && !isAlarm)
                {
                    Toast.makeText(NewReminderActivity.this, "You must select at least one option.", Toast.LENGTH_SHORT).show();
                }
                else if(isNotify && editTextHours.getText().toString().equals(""))
                {
                    Toast.makeText(NewReminderActivity.this, "Set hours!", Toast.LENGTH_SHORT).show();
                }
                else {

                    alarmStatus.setVisibility(View.VISIBLE);
                    notificationStatus.setVisibility(View.VISIBLE);

                    if(isNotify)
                    {
                            hours = Integer.parseInt(editTextHours.getText().toString());
                            notificationStatus.setText("Notification Enabled");
                            notificationStatus.setTextColor(Color.GREEN);

                    }
                    else
                    {
                        notificationStatus.setText("Notification Disabled");
                        notificationStatus.setTextColor(Color.RED);
                    }
                    if(isAlarm)
                    {
                        alarmStatus.setText("Alarm Enabled");
                        alarmStatus.setTextColor(Color.GREEN);
                    }
                    else
                    {
                        alarmStatus.setText("Alarm Disabled");
                        alarmStatus.setTextColor(Color.RED);
                    }

                    dialog.dismiss();
                }

            }
        });


        dialog.show();
    }




    public void setOnBackPress()
    {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isEnabled())
                {
                    getTitle=title.getText().toString();
                    getDescription=description.getText().toString();
                    if(getTitle.equals("")&&getDescription.equals(""))
                    {
                        setEnabled(false);
                        onBackPressed();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewReminderActivity.this);
                        builder.setTitle("Your reminder was not set!").setMessage("Do you still want to close?")
                                .setCancelable(true)
                                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                }

            }
        });
    }











      /* void notificationBuild()
    {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.reminder,null);
        Bitmap largeIcon = bitmapDrawable.getBitmap();
        Notification notification;

        Intent intentNotify = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,100,intentNotify,PendingIntent.FLAG_IMMUTABLE);


        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle()
                .bigLargeIcon(largeIcon)
                .bigLargeIcon(largeIcon)
                .setBigContentTitle(getTitle)
                .setSummaryText(getDescription);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.note_icon)
                    .setContentText("Reminder")
                    .setSubText(getTitle)
                            .setContentIntent(pendingIntent)
                            .setStyle(bigPictureStyle)
                            .setOngoing(true)
                    .setChannelId("Reminder Notification")
                    .build();
                    notificationManager.createNotificationChannel(new NotificationChannel("Reminder Notification","Reminder",NotificationManager.IMPORTANCE_HIGH));

        }
        else
        {
                    notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.note_icon)
                    .setContentText("Reminder")
                    .setSubText(getTitle)
                            .setContentIntent(pendingIntent)
                            .setStyle(bigPictureStyle)
                            .setOngoing(true)
                    .build();
        }
        notificationManager.notify(100,notification);

    }*/

}