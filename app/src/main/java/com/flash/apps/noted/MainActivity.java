package com.flash.apps.noted;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference ourNotesDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mNotesList = findViewById(R.id.main_notes_list);
        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(gridLayoutManager);
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null){
            ourNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        }
        setContentView(R.layout.create_note);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<NoteModel> options = new FirebaseRecyclerOptions.Builder<NoteModel>()
                .setQuery(ourNotesDatabase, NoteModel.class).build();
        FirebaseRecyclerAdapter<NoteModel, NoteViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(options) {
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_note_layout, parent, false);

                return new NoteViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final NoteViewHolder holder, int position, @NonNull NoteModel model) {
                String noteId = getRef(position).getKey();
                ourNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();
                        holder.setNoteTitle(title);
                        holder.setNoteContent(content);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mNotesList.setAdapter(firebaseRecyclerAdapter);
    }
}
