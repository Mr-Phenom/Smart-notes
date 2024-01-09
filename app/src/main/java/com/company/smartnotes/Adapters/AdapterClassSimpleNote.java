package com.company.smartnotes.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.smartnotes.R;
import com.company.smartnotes.Room.RoomSimpleNotes;

import java.util.ArrayList;
import java.util.List;

public class AdapterClassSimpleNote extends RecyclerView.Adapter<AdapterClassSimpleNote.CardViewHolder>{

    //private Context context;
    private List<RoomSimpleNotes> listModel = new ArrayList<>();


   OnLongItemClickListener longItemClickListener;
    onItemClickListener itemClickListener;


    /*public AdapterClassSimpleNote( Context context) {
        this.context = context;
    }*/

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design_notes_simple,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        RoomSimpleNotes currentNote = listModel.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        //Log.d("title",currentNote.getTitle().toString());

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

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public void setNotes(List<RoomSimpleNotes> listModel)
    {
       this.listModel = listModel;
       notifyDataSetChanged();
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longClickListener)
    {
        this.longItemClickListener=longClickListener;
    }

    public interface OnLongItemClickListener{
        void itemLongClicked(View v,int position);
    }

    public RoomSimpleNotes getNote(int position)
    {
        return listModel.get(position);
    }

    public interface onItemClickListener
    {
        void onItemClick(RoomSimpleNotes note);
    }

    public void setOnItemClickListener(onItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle=itemView.findViewById(R.id.textViewTitleCardDesign);
            textViewDescription=itemView.findViewById(R.id.textViewDescriptionCardDesign);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if(itemClickListener!=null && position!=RecyclerView.NO_POSITION)
                    {
                            itemClickListener.onItemClick(listModel.get(position));
                    }
                }
            });
        }
    }
}
