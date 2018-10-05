package com.flash.apps.noted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import java.lang.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


import java.util.HashMap;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    private EditText etTitle, etContent;
    //private Toolbar mToolbar;
    private FirebaseAuth fAuth;
    private Button btnSave;

    String title;
    String content;

    private DatabaseReference ourNoteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);


            etTitle =  findViewById(R.id.etTitle);
            etContent =  findViewById(R.id.etContent);
            btnSave = findViewById(R.id.saveNote);
            //mToolbar = (Toolbar) findViewById(R.id.new_Note_Toolbar);
            //setSupportActionBar(mToolbar);
            fAuth = FirebaseAuth.getInstance();
            ourNoteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    title = etTitle.getText().toString().trim();
                    content = etContent.getText().toString().trim();
                    createNote(title, content);
                }
            });

        /*@Override
        public boolean onCreateOptionsMenu (MenuItem menu){
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
            return super.onCreateOptionsMenu(menu);
        }
*/


        }

        //@Override
        //public void onBackPressed() {

          //  super.onBackPressed();


        //}
        public void createNote(String title, String content) {
            if (fAuth.getCurrentUser() != null) {
            DatabaseReference newNoteRef = ourNoteDatabase.push();
            Map noteMap = new HashMap();
            noteMap.put("Title", title);
            noteMap.put("Content", content);
            noteMap.put("Timestamp", ServerValue.TIMESTAMP);
            newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateNote.this, "NOTE ADDED", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateNote.this, "Error adding to database" , Toast.LENGTH_SHORT).show();
                    }
                }
            });
            } else {
                Toast.makeText(this, "USER IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
            }
        }





    }









