package com.example.john.munchies;
//Harvey Cabrias
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerCreditCardList extends AppCompatActivity {

    FirebaseDatabase myFB;
    DatabaseReference myRef;

    ListView customerCreditCardList;
    ArrayList<String> customerCreditCardArrayList;
    ArrayAdapter<String> customerCreditCardAdapter;

    String userEmail;
    String customerEmail;
    Button btnDeleteCreditCard;
    Button btnAddCreditCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_credit_card_list);

        customerCreditCardList = (ListView)findViewById(R.id.customerCreditCardList);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userEmail = sharedPref.getString("customerEmail", "");

        myFB = FirebaseDatabase.getInstance();
        myRef = myFB.getReference("MunchiesDB").child("CustomerCreditCards");

        customerCreditCardArrayList = new ArrayList<String>();

        btnDeleteCreditCard = (Button)findViewById(R.id.btnDeleteCardList);
        btnAddCreditCard = (Button)findViewById(R.id.btnAddCreditCard);

        displayCreditCardList();
        deleteCreditCard();
        MyIntent();
    }




    //Display the CreditCard from the database
    public void displayCreditCardList(){
        String getCustomerCreditCardEmail = userEmail.toString();
        customerEmail = getCustomerCreditCardEmail.substring(0, getCustomerCreditCardEmail.indexOf("@"));

        myRef = myFB.getReference("MunchiesDB").child("CustomerCreditCards").child(customerEmail);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showData(DataSnapshot dataSnapshot){
        customerCreditCardArrayList.clear();
        for(DataSnapshot cards: dataSnapshot.getChildren()){
            CreditCardClass creditCard = cards.getValue(CreditCardClass.class);
            customerCreditCardArrayList.add(creditCard.getCreditCardNumber());
        }

        customerCreditCardAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, customerCreditCardArrayList);
        customerCreditCardList.setAdapter(customerCreditCardAdapter);

    }

    public void deleteCreditCard(){
        final CreditCardClass creditCard = new CreditCardClass();
        customerCreditCardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                creditCard.setCreditCardID(customerCreditCardArrayList.get(position));
            }
        });

        btnDeleteCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = creditCard.getCreditCardID();
                //Toast.makeText(getApplicationContext(), "Deleted " +  name  + " item menu", Toast.LENGTH_SHORT).show();
                if(id.equals("")){
                    Toast.makeText( CustomerCreditCardList.this, "Please Select credit card you before delete!", Toast.LENGTH_LONG).show();
                } else {
                    myRef.child(customerEmail).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            myRef.child(id).removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    public void MyIntent(){
        btnAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CustomerCreditCardList.this, CustomerCreditCard.class);
                startActivity(myIntent);
            }
        });
    }
}
