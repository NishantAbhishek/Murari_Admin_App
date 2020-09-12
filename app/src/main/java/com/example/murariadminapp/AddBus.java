package com.example.murariadminapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.murariadminapp.Interface.IFirebaseLoadDone;
import com.example.murariadminapp.Model.IDs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddBus extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener, IFirebaseLoadDone {
    private Button AB_BusStartTime,AB_BusEndTime,AB_AddBusbtn;
    private EditText  AB_Busnoedt,AB_Busticket_price;
    private TextView AB_Name;
    private DatabaseReference admin_name,locationref;
    private ProgressDialog Initilizer_PD,sendindData;
    private String String_name;
    int hour,minute,hourfinal,minutefinal;
    int day,month,year,dayfinal,monthfinal,yearfinal;
    private boolean ChooseStarttime = true;
    private String finalRunnningDate,busType;
    private String dbStarttime,dbEndtime,dbDate;
    private Spinner AB_Spinner_Bustype;
    private DatabaseReference StoringData,FinalStore;

    private SearchableSpinner AB_BusPinStart,AB_BusPinEnd;

    IFirebaseLoadDone iFirebaseLoadDone;
    String ST_AB_BusPinStart,ST_AB_BusPinEnd;
    private TextView TV_AB_BusPinStart,TV_AB_BusPinEnd;
    private EditText  AB_NoOfSeat;


    List<IDs> iDs;

    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        Initialize();
        BusStartTime();
        BusDestinationTime();
        FirebaseDataRetrieve();
        SpinnerGetText();
        Confirming();
        busAc_NonAc();

        Button Refresh = (Button)  findViewById(R.id.AB_Refresh);
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TV_AB_BusPinStart.setText("");
                TV_AB_BusPinEnd.setText("");
                AB_Busnoedt.setText("");
                AB_BusStartTime.setText("Departure Time");
                AB_BusEndTime.setText("Arrival Time");


            }
        });


    }



    private void FirebaseDataRetrieve(){
        locationref = FirebaseDatabase.getInstance().getReference("Locations");
        iFirebaseLoadDone = this;
        locationref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<IDs> iDs = new ArrayList<>();

                for (DataSnapshot idSnapShot:dataSnapshot.getChildren()){
                    iDs.add(idSnapShot.getValue(IDs.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(iDs);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }



    private void SpinnerGetText(){
        AB_BusPinStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IDs iD = iDs.get(position);
                ST_AB_BusPinStart = iD.getLocation_pin();
                TV_AB_BusPinStart.setText(ST_AB_BusPinStart);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {




            }
        });
        AB_BusPinEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IDs iD = iDs.get(position);
                ST_AB_BusPinEnd = iD.getLocation_pin();
                TV_AB_BusPinEnd.setText(ST_AB_BusPinEnd);




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });





    }

    private void Initialize(){
        admin_name = FirebaseDatabase.getInstance().getReference().child("Admin");
        dbDate = "Runs Everyday";

        radioGroup = findViewById(R.id.radioGroup);
        AB_Name = findViewById(R.id.AB_Name);

        //Button
        AB_BusStartTime = findViewById(R.id.AB_BusStartTime);
        AB_BusEndTime = findViewById(R.id.AB_BusEndTime);
        AB_AddBusbtn = findViewById(R.id.AB_AddBusbtn);

        //Edittext
        AB_Busnoedt = findViewById(R.id.AB_Busnoedt);
        AB_BusPinStart = findViewById(R.id.AB_BusPinStart);
        AB_BusPinEnd = findViewById(R.id.AB_BusPinEnd);

        TV_AB_BusPinEnd = findViewById(R.id.TV_AB_BusPinEnd);
        TV_AB_BusPinStart = findViewById(R.id.TV_AB_BusPinStart);

        AB_Busticket_price = findViewById(R.id.AB_Price_Ticket);


        Initilizer_PD = new ProgressDialog(this);
        Initilizer_PD.setTitle("Connecting to database");
        Initilizer_PD.show();

        sendindData = new ProgressDialog(this);
        sendindData.setTitle("Saving data to database");
        sendindData.setCancelable(false);

        AB_NoOfSeat = findViewById(R.id.AB_NoOfSeat);


        admin_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String_name = dataSnapshot.child("Name").getValue().toString();
                AB_Name.setText(String_name);
                Initilizer_PD.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        finalRunnningDate = radioButton.getText().toString();

        if(finalRunnningDate.equals("Specific Days")){
            Confirmdays();
        }
        dbDate = finalRunnningDate;
        Toast.makeText(this,"dbDate"+dbDate,Toast.LENGTH_SHORT).show();
    }

    //Start the selection of start time
    private void BusStartTime(){
        AB_BusStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseStarttime =true;
                Selecttime();

            }
        });
    }

    private void BusDestinationTime(){
        AB_BusEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseStarttime = false;
                Selecttime();

            }
        });



    }

    private void Confirmdays(){
        if(finalRunnningDate.equals("Specific Days")){
            Calendar c = Calendar.getInstance();
            day = c.get(Calendar.DAY_OF_MONTH);
            month = c.get(Calendar.MONTH);
            year = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddBus.this,AddBus.this,
                    year,month,day);
            datePickerDialog.show();



        }



    }


    //Here it is used to start the time dialog box
    private void Selecttime(){
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddBus.this,
                AddBus.this,hour,minute,
                DateFormat.is24HourFormat(AddBus.this));
        timePickerDialog.show();


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearfinal =year;
        monthfinal = month+1;
        dayfinal = dayOfMonth;
        dbDate = ("Date:" + dayfinal + "_" + monthfinal +"_" + yearfinal);
        Toast.makeText(AddBus.this," dbDate "+dbDate , Toast.LENGTH_SHORT).show();




    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hourfinal = hourOfDay;
        minutefinal = minute;
        if(ChooseStarttime == true){
            AB_BusStartTime.setText("Starting at : "+hourfinal+" : "+minutefinal);
            dbStarttime = (hourfinal+" : "+minutefinal);
        }
        if(ChooseStarttime==false){
            AB_BusEndTime.setText("Arriving at : "+hourfinal+" : "+minutefinal);
            dbEndtime = (hourfinal+" : "+minutefinal) ;

        }




    }

    private void busAc_NonAc(){
        AB_Spinner_Bustype = findViewById(R.id.AB_Spinner_Bustype);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.BusType,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AB_Spinner_Bustype.setAdapter(adapter);
        AB_Spinner_Bustype.setOnItemSelectedListener(this);

    }

    private void Confirming(){
        AB_AddBusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busAc_NonAc();
                String dbBusNumber = AB_Busnoedt.getText().toString();
                String dbLocationPinDeparture = TV_AB_BusPinStart.getText().toString();
                String dbLocationPinDestination = TV_AB_BusPinEnd.getText().toString();
                String AB_NoOfSeatSt = AB_NoOfSeat.getText().toString();

                if(!dbLocationPinDeparture.equals(dbLocationPinDestination)){

                    if(!busType.isEmpty() && !dbBusNumber.isEmpty() && !dbLocationPinDeparture.isEmpty()
                            &&!dbLocationPinDestination.isEmpty() &&!dbStarttime.isEmpty()&&!AB_NoOfSeatSt.isEmpty()){

                        sendindData.show();
                        SendBusData(busType,dbBusNumber,dbLocationPinDeparture,dbLocationPinDestination,dbStarttime,dbEndtime,dbDate,AB_NoOfSeatSt);

                    }else {
                        Toast.makeText(AddBus.this,"Please fill each box",Toast.LENGTH_SHORT).show();

                    }

                }else {
                    Toast.makeText(AddBus.this,"Location is repeated",Toast.LENGTH_SHORT).show();


                }






            }
        });

    }

    private void SendBusData(String busType,String dbBusNumber,
                             String dbLocationPinDeparture,String dbLocationPinDestination,
                             String dbStarttime,String dbEndtime,String dbDate,String busno){
        String  DateId = (dbDate + dbLocationPinDeparture + dbLocationPinDestination);
        StoringData = FirebaseDatabase.getInstance().getReference().
                child("Buses").child(DateId).child(dbBusNumber);
        String Ticket_price = AB_Busticket_price.getText().toString();
        HashMap<String, String> loci = new HashMap<>();
        loci.put("DeparturePin",dbLocationPinDeparture);
        loci.put("ArrivalPin",dbLocationPinDestination);
        loci.put("DepartureTime",dbStarttime);
        loci.put("ArrivalTime",dbEndtime);
        loci.put("Date",dbDate);
        loci.put("TypeSit",busType.toLowerCase());//Converting text to lower case
        loci.put("BusNo",dbBusNumber);
        loci.put("NumberOfSeat",busno);
        loci.put("TicketPrice",Ticket_price);
        StoringData.setValue(loci).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendindData.dismiss();
                    Toast.makeText(AddBus.this,"Success" ,Toast.LENGTH_SHORT).show();
                }else {
                    sendindData.dismiss();
                    Toast.makeText(AddBus.this,"Try again" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         busType =parent.getItemAtPosition(position).toString();
         Toast.makeText(AddBus.this,busType,Toast.LENGTH_SHORT);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onFirebaseLoadSuccess(List<IDs> Locationlist) {
        iDs = Locationlist;
        List<String> id_list= new ArrayList<>();
        for(IDs id:Locationlist){
            id_list.add(id.getPlace());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,id_list);
            AB_BusPinStart.setAdapter(adapter);
            AB_BusPinEnd.setAdapter(adapter);
        }
    }




    @Override
    public void onFirebaseLoadFailed(String Message) {

        Toast.makeText(AddBus.this,Message,Toast.LENGTH_LONG).show();


    }
}
