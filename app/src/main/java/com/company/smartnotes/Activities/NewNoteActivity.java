package com.company.smartnotes.Activities;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.smartnotes.R;

public class NewNoteActivity extends AppCompatActivity {

    EditText title,description;

    String getTitle,getDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        setContentView(R.layout.activity_new_note);

        title = findViewById(R.id.editTextTitleNewNote);
        description = findViewById(R.id.editTextDescriptionNewNote);

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
            {
                getTitle=title.getText().toString();
                getDescription=description.getText().toString();
                if(getTitle.equals("") || getDescription.equals(""))
                {
                    Toast.makeText(NewNoteActivity.this, "Title and description can not be Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
//                    SimpleNoteDatabaseHelper helper = SimpleNoteDatabaseHelper.getDInstance(NewNoteActivity.this);
//                    RoomSimpleNoteDAO dao = helper.roomSimpleNoteDAO();
//                    dao.addNotes(new RoomSimpleNotes(getTitle,getDescription));
                    Intent i = new Intent();
                    i.putExtra("title",getTitle);
                    i.putExtra("description",getDescription);
                    setResult(RESULT_OK,i);
                    finish();


                }
            }
                return true;
        }

       else if(item.getItemId()==android.R.id.home)
       {
           getTitle=title.getText().toString();
           getDescription=description.getText().toString();
           if(getTitle.equals("")&&getDescription.equals(""))
           {
               finish();
           }
           else
           {

               Toast.makeText(this, "Note was not saved!", Toast.LENGTH_LONG).show();
           }
       }

       else
       {
               return super.onOptionsItemSelected(item);
       }

        return false;
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewNoteActivity.this);
                        builder.setTitle("Your note was not saved!").setMessage("Do you still want to close?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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

}