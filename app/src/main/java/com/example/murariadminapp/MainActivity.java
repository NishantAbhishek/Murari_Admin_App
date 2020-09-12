package com.example.murariadminapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView adminName;
    private Button AddLocationtbtn,AddBus,SeeTickets;
    private ProgressDialog progressDialog,progresslocation;
    private String Name,Location,PinCode,State,States;
    private DatabaseReference databaseName,databaseAddress;
    private EditText edt_Location,edt_PinNo;
    private Spinner State_instance_Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progresslocation = new ProgressDialog(this);
        Initialize();
        Buttons();

        progressDialog.setTitle("Configuring environment");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }


    private void Initialize(){
        AddLocationtbtn = findViewById(R.id.MN_AddLocationbtn);


        adminName = findViewById(R.id.MN_Name);

        AddBus = findViewById(R.id.MN_AddBus);
        SeeTickets = findViewById(R.id.MN_Tickets);
        AddLocationtbtn = findViewById(R.id.MN_AddLocationbtn);


        //edittext
        edt_Location = findViewById(R.id.MN_AddLocationedt);
        edt_PinNo = findViewById(R.id.MN_LocationPinedt);


        //So first I created an array of data.
        String[] State = new String[]{"","Bihar","Jharkhand","Orissa"};
        //State_instance_Spinner is instance used in Initilize method.
        State_instance_Spinner =  findViewById(R.id.MN_LocationStateedt);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,State);
        State_instance_Spinner.setAdapter(adapter);
        State_instance_Spinner.setOnItemSelectedListener(MainActivity.this);
        //Here I am placing the value from array to the Spinner or you can say it as drop down menu.

        databaseName = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseAddress = FirebaseDatabase.getInstance().getReference().child("Location");



        databaseName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = dataSnapshot.child("Name").getValue().toString();
                adminName.setText(Name);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,Name,Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        State = parent.getItemAtPosition(position).toString();
        Toast.makeText(MainActivity.this,State,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private void Buttons(){
        AddBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeedt();
                Intent in = new Intent(MainActivity.this,AddBus.class);
                startActivity(in);




            }
        });


        SeeTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this,Admin.class);
                startActivity(in);

            }
        });
        AddLocationtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location = edt_Location.getText().toString();
                PinCode = edt_PinNo.getText().toString();
                if(!Location.isEmpty() && !PinCode.isEmpty() && !State.equals(null)){
                    progresslocation.setTitle("Registering");
                    progresslocation.setCancelable(false);
                    progresslocation.show();
                    SetLocation(Location,PinCode,State);
                }else {
                    Toast.makeText(MainActivity.this,"Please fill each box",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetLocation(String Location,String PinCode,String State){

        databaseAddress = FirebaseDatabase.getInstance().getReference().child("Locations").child(PinCode);
        HashMap<String, String> loci = new HashMap<>();
       // Integer PinCode_no =  Integer.parseInt(PinCode);
        loci.put("Location_pin",PinCode);
        loci.put("Place",Location.toUpperCase());
        loci.put("State",State.toUpperCase());
        databaseAddress.setValue(loci).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Location Updated in database",Toast.LENGTH_SHORT).show();
                    progresslocation.dismiss();
                    removeedt();
                }else {
                    Toast.makeText(MainActivity.this,"Location not updated ",Toast.LENGTH_SHORT).show();
                    progresslocation.dismiss();
                }
            }
        });
    }
    private void removeedt(){
        edt_PinNo.setText("");
        edt_Location.setText("");
    }



}
