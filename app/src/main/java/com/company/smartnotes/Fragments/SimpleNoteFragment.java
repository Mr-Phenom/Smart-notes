package com.company.smartnotes.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.UiccPortInfo;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.company.smartnotes.Activities.NewNoteActivity;
import com.company.smartnotes.Activities.UpdateNoteActivity;
import com.company.smartnotes.Adapters.AdapterClassSimpleNote;
import com.company.smartnotes.ModelClass.SImpleNoteViewModel;
import com.company.smartnotes.R;
import com.company.smartnotes.Room.RoomSimpleNotes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class SimpleNoteFragment extends Fragment {

    public static SimpleNoteFragment getInstance()
    {
        return new SimpleNoteFragment();
    }


    AdapterClassSimpleNote adapter;
    private int currPosition;
    private SImpleNoteViewModel sImpleNoteViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    ActivityResultLauncher<Intent> activityResultLauncherForAddNote;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateNote;


    public SimpleNoteFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_simple_note, container, false);

        fab = rootView.findViewById(R.id.floatingActionButton);

        registerActivityForAddNote();
        registerActivityForUpdateNote();

        recyclerView = rootView.findViewById(R.id.recyclerViewSimpleNote);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new AdapterClassSimpleNote();
        recyclerView.setAdapter(adapter);

        sImpleNoteViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())
                .create(SImpleNoteViewModel.class);
        sImpleNoteViewModel.getAllnotes().observe(getActivity(), new Observer<List<RoomSimpleNotes>>() {
            @Override
            public void onChanged(List<RoomSimpleNotes> roomSimpleNotes) {

                adapter.setNotes(roomSimpleNotes);
                Log.d("SimpleNoteFragment", "Data changed: " + roomSimpleNotes.size() + " items");
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewNoteActivity.class);
                //startActivity(i);
                activityResultLauncherForAddNote.launch(i);

            }
        });


        adapter.setOnLongItemClickListener(new AdapterClassSimpleNote.OnLongItemClickListener() {
            @Override
            public void itemLongClicked(View v, int position) {
                currPosition=position;
            }
        });

        adapter.setOnItemClickListener(new AdapterClassSimpleNote.onItemClickListener() {
            @Override
            public void onItemClick(RoomSimpleNotes note) {
                Intent i = new Intent(getActivity(), UpdateNoteActivity.class);
                i.putExtra("idForUpdate",note.getId());
                i.putExtra("titleForUpdate",note.getTitle());
                i.putExtra("descriptionForUpdate",note.getDescription());
                activityResultLauncherForUpdateNote.launch(i);

            }
        });

        registerForContextMenu(recyclerView);



        return rootView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.popup_option_menu,menu);
    }

//    @Override
//    @SuppressLint("RestrictedApi")
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        requireActivity().getMenuInflater().inflate(R.menu.popup_option_menu,menu);
//        if(menu instanceof MenuBuilder)
//        {
//             MenuBuilder m = (MenuBuilder) menu;
//             m.setOptionalIconsVisible(true);
//
//        }
//    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


       if(item.getItemId()==R.id.option_delete)
       {
           AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
           builder.setTitle("Delete").setMessage("Are you sure you want to delete?")
                   .setCancelable(true)
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           sImpleNoteViewModel.delete(adapter.getNote(currPosition));
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

    public void registerActivityForAddNote()
    {
        activityResultLauncherForAddNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        int resultCode = o.getResultCode();
                        Intent data = o.getData();
                        if(resultCode == RESULT_OK && data!=null)
                        {
                            String title = data.getStringExtra("title");
                            String description = data.getStringExtra("description");

                            RoomSimpleNotes note = new RoomSimpleNotes(title,description);
                            sImpleNoteViewModel.insert(note);
                        }
                    }
                });
    }

    public void registerActivityForUpdateNote()
    {
        activityResultLauncherForUpdateNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                        int resultCode = o.getResultCode();
                        Intent data = o.getData();
                        if(resultCode==RESULT_OK && data!=null)
                        {
                            String t=data.getStringExtra("titleNewFromUpdate");
                            String d = data.getStringExtra("descriptionNewFromUpdate");
                            int id = data.getIntExtra("idNewFromUpdate",-1);
                            RoomSimpleNotes note = new RoomSimpleNotes(t,d);
                            note.setId(id);
                            sImpleNoteViewModel.update(note);
                        }

                    }
                });
    }
}