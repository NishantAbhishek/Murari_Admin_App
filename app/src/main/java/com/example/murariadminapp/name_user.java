package com.example.murariadminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class name_user extends AppCompatActivity {
    ArrayList<String> Tickets_name = new ArrayList<>();
    private ListView myListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_user);

        myListview = findViewById(R.id.NM_listView);

        final ArrayAdapter<String> myArrayAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Tickets_name);
        myListview.setAdapter(myArrayAdapter);

        String intent_ref = getIntent().getStringExtra("database_ref");
        Toast.makeText(name_user.this, intent_ref ,Toast.LENGTH_LONG).show();

        DatabaseReference Ticket_Check_User_Admin = FirebaseDatabase.getInstance().
                getReference().child("Tickets").child("Ticket_Check_User_Admin").child(intent_ref);


        Ticket_Check_User_Admin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String myChildViews = dataSnapshot.getValue(String.class);
                Tickets_name.add(myChildViews);
                myArrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }
}
