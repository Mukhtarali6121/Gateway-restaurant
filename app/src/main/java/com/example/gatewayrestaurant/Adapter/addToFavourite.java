package com.example.gatewayrestaurant.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gatewayrestaurant.R;
import com.example.gatewayrestaurant.model.model;
import com.example.gatewayrestaurant.model.model1;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class addToFavourite extends FirebaseRecyclerAdapter<model1,addToFavourite.myviewholder> {



    public addToFavourite(@NonNull FirebaseRecyclerOptions<model1> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final addToFavourite.myviewholder holder, final int position, @NonNull final model1 model) {
        Glide.with(holder.ivMenuImage.getContext()).load(model.getImage()).into(holder.ivMenuImage);
        holder.tvMenuTitle.setText(model.getName());
        holder.tvPrice.setText("\u20B9 "+String.valueOf(model.getPrice()));

        holder.ivAddToFav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("position", String.valueOf(position));
                removeFromFav(position, model);

            }
        });

    }

    @NonNull
    @Override
    public addToFavourite.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.add_to_fav_cardview,parent,false);
        return new myviewholder(view);
    }


    public class myviewholder extends RecyclerView.ViewHolder {
        ImageView ivMenuImage, ivAddToFav1;
        TextView tvMenuTitle, tvPrice;
//        Button btnAddToCart;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivAddToFav1 = itemView.findViewById(R.id.ivAddToFav1 );
//            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

    }



    private void removeFromFav( int position, model1 model){

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        String dishName =model.getName();

        DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("favourite").child(uid);
        Query query = databaseReference.orderByChild("name").equalTo(dishName);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
