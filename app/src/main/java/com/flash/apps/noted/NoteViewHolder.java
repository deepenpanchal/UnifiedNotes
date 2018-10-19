package com.flash.apps.noted;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView textTitle, textContent;
    CardView noteCard;
    public NoteViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        textTitle = mView.findViewById(R.id.note_title);
        textContent = mView.findViewById(R.id.note_content);
        noteCard = mView.findViewById(R.id.note_card);
    }
    public void setNoteTitle(String title){
        textTitle.setText(title);
    }
    public void setNoteContent(String content){
        textContent.setText(content);
    }
}
