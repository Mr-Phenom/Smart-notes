package com.company.smartnotes.Activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class UpdateReminderActivity extends AppCompatActivity {

    EditText title,description;
    TextView notificationStatus,alarmStatus;
    Button timePicker;
    String getTitleOld,getDescriptionOld,getTitleNew,getDescriptionNew;
    String getDateOld,getDateNew;
    int id;
    int currDay,currMonth,currYear,selectDay=-1,selectMonth=-1,selectYear=-1,selectHour=-1,selectMinute=-1;
    int hours=-1;
    boolean isNotifyOld,isAlarmOld,isNotifyNew,isAlarmNew;
    boolean timeChange = false;
    long notificationTImeOld,selectTimeOld,notificationTimeNew,selectTimeNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);

        title = findViewById(R.id.editTextTitleUpdateReminder);
        description = findViewById(R.id.editTextDescriptionUpdateReminder);
        notificationStatus=findViewById(R.id.textViewNotificationStatusUpdateReminder);
        alarmStatus = findViewById(R.id.textViewAlarmStatusUpdateReminder);
        timePicker = findViewById(R.id.buttonTimePickerUpdateReminder);
        Toolbar toolbar = findViewById(R.id.toolbar_update_reminder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Reminder");


        Intent i = getIntent();
        id = i.getIntExtra("idForUpdateReminder",-1);
        getTitleOld = i.getStringExtra("titleForUpdateReminder");
        getDescriptionOld = i.getStringExtra("descriptionForUpdateReminder");
        getDateOld =  i.getStringExtra("timeForUpdateReminder");
        notificationTImeOld = i.getLongExtra("notificationTimeForUpdateReminder",0);
        selectTimeOld = i.getLongExtra("selectTimeForUpdateReminder",0);
        isNotifyOld = i.getBooleanExtra("isNotifyForUpdateReminder",false);
        isAlarmOld = i.getBooleanExtra("isAlarmForUpdateReminder",false);

        title.setText(getTitleOld);
        description.setText(getDescriptionOld);


        isNotifyNew = isNotifyOld;
        isAlarmNew=isAlarmOld;
        notificationTimeNew=notificationTImeOld;
        selectTimeNew=selectTimeOld;

        if(isNotifyOld)
        {
            notificationStatus.setText("Notification Enabled");
            notificationStatus.setTextColor(Color.GREEN);
        }
        else
        {
            notificationStatus.setText("Notification Disabled");
            notificationStatus.setTextColor(Color.RED);
        }

        if(isAlarmOld)
        {
            alarmStatus.setText("Alarm Enabled");
            alarmStatus.setTextColor(Color.GREEN);
        }
        else
        {
            alarmStatus.setText("Alarm Disabled");
            alarmStatus.setTextColor(Color.RED);
        }

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                currDay=calendar.get(Calendar.DAY_OF_MONTH);
                currMonth=calendar.get(Calendar.MONTH);
                currYear=calendar.get(Calendar.YEAR);

                dateDialogue();
            }
        });

//        notificationStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               settingsDialogue();
//            }
//        });
//        alarmStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                settingsDialogue();
//            }
//        });

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
            updateReminder();
        }
        return true;
    }


    public void updateReminder()
    {
        getTitleNew = title.getText().toString();
        getDescriptionNew = description.getText().toString();
        getDateNew = selectYear+"/"+selectMonth+"/"+selectDay+" "+selectHour+":"+selectMinute;
        if(isNotifyNew!=isNotifyOld || isAlarmOld!=isAlarmNew)
        {
            timeChange = true;
        }

        if(getTitleNew.equals("") || getDescriptionNew.equals(""))
        {
            Toast.makeText(UpdateReminderActivity.this, "Title and description can not be Empty", Toast.LENGTH_SHORT).show();
        }

        else if(timeChange = true){

            String selectedDate = selectYear + "/" + selectMonth + "/" + selectDay + " " + selectHour + ":" + selectMinute + ":00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                Date date = sdf.parse(selectedDate);
                selectTimeNew = date.getTime();
                notificationTimeNew = date.getTime() - hours * 60 * 60 * 1000;

                // alarmManagerBuild(millis, date.getTime());
            } catch (ParseException e) {
                Toast.makeText(this, "Uhu exception", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }

        }

        Intent i = new Intent();

        i.putExtra("titleFromUpdateReminder",getTitleNew);
        i.putExtra("descriptionFromUpdateReminder",getDescriptionNew);
        i.putExtra("timeFromUpdateReminder",getDateNew);
        i.putExtra("notificationTimeFromUpdateReminder",notificationTimeNew);
        i.putExtra("selectTimeFromUpdateReminder",selectTimeNew);
        i.putExtra("isNotifyFromUpdateReminder",isNotifyNew);
        i.putExtra("isAlarmFromUpdateReminder",isAlarmNew);

        if(id!=-1)
        {
            i.putExtra("idFromUpdateReminder",id);
            setResult(RESULT_OK,i);
            Toast.makeText(this, "Reminder Updated", Toast.LENGTH_SHORT).show();
            finish();
        }

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
                //Toast.makeText(UReminderActivity.this, "You must set a date for your event.", Toast.LENGTH_LONG).show();
                datePicker.cancel();
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
                    getDateNew = (selectDay + "/" + selectMonth + "/" + selectYear + " at "+ selectHour + " : " + selectMinute+" PM");
                }
                else
                {
                    convertedHour=selectHour;
                    if(convertedHour==0)
                        convertedHour=12;
                    getDateNew = (selectDay + "/" + selectMonth + "/" + selectYear + " at "+ selectHour + " : " + selectMinute+" AM");
                }

                settingsDialogue();

            }
        },12,00,false);
        timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(UpdateReminderActivity.this, "You must set a time", Toast.LENGTH_SHORT).show();
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

        if(isAlarmOld)
        {
            checkBoxAlarm.setChecked(true);
        }
        if(isNotifyOld)
        {
            checkBoxNotification.setChecked(true);
        }


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

                isAlarmNew=checkBoxAlarm.isChecked();
                isNotifyNew=checkBoxNotification.isChecked();


                if (!isNotifyNew && !isAlarmNew)
                {
                    Toast.makeText(UpdateReminderActivity.this, "You must select at least one option.", Toast.LENGTH_SHORT).show();
                }
                else if(isNotifyNew && editTextHours.getText().toString().equals(""))
                {
                    Toast.makeText(UpdateReminderActivity.this, "Set hours!", Toast.LENGTH_SHORT).show();
                }
                else {

                    alarmStatus.setVisibility(View.VISIBLE);
                    notificationStatus.setVisibility(View.VISIBLE);

                    if(isNotifyNew)
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
                    if(isAlarmNew)
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
                    getTitleNew=title.getText().toString();
                    getDescriptionNew=description.getText().toString();
                    if(getTitleNew.equals(getTitleOld) && getDescriptionNew.equals(getDescriptionOld) && isAlarmNew==isAlarmOld && isNotifyNew==isNotifyOld && notificationTimeNew==notificationTImeOld && selectTimeNew==selectTimeOld )
                    {
                        setEnabled(false);
                        onBackPressed();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateReminderActivity.this);
                        builder.setTitle("Update Reminder").setMessage("Do you want to update your Reminder?")
                                .setCancelable(true)
                                .setPositiveButton("Update and Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        updateReminder();
                                    }
                                }).setNegativeButton("Don't save & Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                }

            }
        });

        timeChange = true;
    }
}
