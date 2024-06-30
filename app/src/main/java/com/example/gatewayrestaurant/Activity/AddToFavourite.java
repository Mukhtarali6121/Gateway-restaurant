package com.example.gatewayrestaurant.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import com.example.gatewayrestaurant.Adapter.addToFavourite;
import com.example.gatewayrestaurant.R;
import com.example.gatewayrestaurant.model.model1;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AddToFavourite extends AppCompatActivity {

    private RecyclerView rvFavourite;
    private addToFavourite adapter;
    private ActionBarDrawerToggle mToggle;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_favourite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favourite");

        rvFavourite = findViewById(R.id.rvFavourite);
        rvFavourite.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



        firebasedata();
    }

    public void firebasedata() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        FirebaseRecyclerOptions<model1> option =
                new FirebaseRecyclerOptions.Builder<model1>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("favourite").child(uid), model1.class)
                        .build();

        adapter = new addToFavourite(option);
        rvFavourite.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        adapter.startListening();
        super.onStart();
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddToFavourite.this,HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}