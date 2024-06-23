package com.example.gatewayrestaurant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gatewayrestaurant.R;
import com.example.gatewayrestaurant.model.model2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class offerAdapter extends FirebaseRecyclerAdapter<model2, offerAdapter.myviewholder> {

    private Context mContext;


    public offerAdapter(@NonNull FirebaseRecyclerOptions<model2> options,  Context context) {
        super(options);
        this.mContext = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull offerAdapter.myviewholder holder, int position, @NonNull model2 model2) {
        if (model2.getImage().equals("")) {
            Glide.with(mContext).load(R.drawable.img_placeholder).into(holder.ivOfferImage);
        } else
            Glide.with(holder.ivOfferImage.getContext()).load(model2.getImage()).into(holder.ivOfferImage);
        holder.tvItemOfferName.setText(model2.getName());
        holder.tvDiscountPrice.setText("\u20B9" + model2.getDiscountprice());
        holder.tvRealPrice.setText("\u20B9" + model2.getRealprice());
//        holder.tvRealPrice.setPaintFlags(holder.tvRealPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


    }

    @NonNull
    @Override
    public offerAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item, parent, false);
        return new offerAdapter.myviewholder(view);

    }

    class myviewholder extends RecyclerView.ViewHolder {
        ImageView ivOfferImage;
        TextView tvItemOfferName, tvDiscountPrice, tvRealPrice;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            ivOfferImage = (ImageView) itemView.findViewById(R.id.ivOfferImage);
            tvItemOfferName = (TextView) itemView.findViewById(R.id.tvItemOfferName);
            tvDiscountPrice = (TextView) itemView.findViewById(R.id.tvDiscountPrice);
            tvRealPrice = (TextView) itemView.findViewById(R.id.tvRealPrice);
        }
    }
}
