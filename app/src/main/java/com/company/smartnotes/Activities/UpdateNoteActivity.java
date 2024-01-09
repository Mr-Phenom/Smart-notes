package com.company.smartnotes.Activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.company.smartnotes.R;

public class UpdateNoteActivity extends AppCompatActivity {

    EditText title,description;
    String titleOld,descriptionOld,descriptionNew,titleNew;
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");

        title =findViewById(R.id.editTextTitleUpdateNote);
        description=findViewById(R.id.editTextDescriptionUpdateNote);

        Intent i = getIntent();
        id = i.getIntExtra("idForUpdate",-1);
        titleOld = i.getStringExtra("titleForUpdate");
        descriptionOld = i.getStringExtra("descriptionForUpdate");

        title.setText(titleOld);
        description.setText(descriptionOld);
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
            titleNew=title.getText().toString();
            descriptionNew=description.getText().toString();
            if(titleNew.equals(titleOld) && descriptionNew.equals(descriptionOld))
            {
                finish();
            }
            else
            {
                updateNote();
            }
        }
        return true;
    }

    public void setOnBackPress()
    {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isEnabled())
                {
                    titleNew=title.getText().toString();
                    descriptionNew=description.getText().toString();
                    if(titleNew.equals(titleOld) && descriptionNew.equals(descriptionOld))
                    {
                        setEnabled(false);
                        onBackPressed();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateNoteActivity.this);
                        builder.setTitle("Update Note").setMessage("Do you want to update your note?")
                                .setCancelable(true)
                                .setPositiveButton("Update and Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        updateNote();
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
    }

    public void updateNote()
    {
        Intent intent = new Intent();
        intent.putExtra("titleNewFromUpdate",titleNew);
        intent.putExtra("descriptionNewFromUpdate",descriptionNew);

        if(id!=-1)
        {
            intent.putExtra("idNewFromUpdate",id);
            setResult(RESULT_OK,intent);
            Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

}