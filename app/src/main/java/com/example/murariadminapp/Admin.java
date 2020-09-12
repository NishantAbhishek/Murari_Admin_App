package com.example.murariadminapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Admin extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private ListView myListview;
    ArrayList<String> Tickets_user = new ArrayList<>();
    private TextView AD_Select_date;
    int day, month, year, dayfinal, monthfinal, yearfinal;
    String dbDate = "nothing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        myListview = findViewById(R.id.AD_listView);
        AD_Select_date = findViewById(R.id.AD_Select_date);

        Buttons();

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearfinal = year;
        monthfinal = month + 1;
        dayfinal = dayOfMonth;
        dbDate = ("Date:" + dayfinal + "_" + monthfinal + "_" + yearfinal);
        retrieve_data();
    }

    private void Buttons() {


        AD_Select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Admin.this,
                        Admin.this, year, month, day);
                datePickerDialog.show();

            }
        });


        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String database_ref = Tickets_user.get(i);
                Toast.makeText(Admin.this, database_ref, Toast.LENGTH_LONG).show();
                Intent in = new Intent(Admin.this, name_user.class);
                in.putExtra("database_ref", database_ref);
                startActivity(in);
            }
        });
    }


    private void retrieve_data(){
        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Tickets_user);
        myListview.setAdapter(myArrayAdapter);

        if(dbDate.equals("nothing")){
            Toast.makeText(Admin.this,dbDate, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Admin.this,dbDate, Toast.LENGTH_SHORT).show();

            DatabaseReference TicketID = FirebaseDatabase.getInstance().getReference().
                    child("Tickets").child("TicketID").child(dbDate);
            TicketID.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String myChildViews = dataSnapshot.getValue(String.class);
                    Tickets_user.add(myChildViews);
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


}
