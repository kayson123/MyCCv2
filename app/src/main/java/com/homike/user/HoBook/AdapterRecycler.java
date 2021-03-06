package com.homike.user.HoBook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by User on 1/2/2017.
 */

public class AdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<WarehouseSalesDetails> data = Collections.emptyList();
    WarehouseSalesDetails current;
    int currentPos = 0;

        //to initialize context and data sent from MainActivity
    public AdapterRecycler(Context context, List<WarehouseSalesDetails> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        notifyDataSetChanged();
    }


    //inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.item_listview,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    //Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        //get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        WarehouseSalesDetails current = data.get(position);
        myHolder.textName.setText(current.company_name);
        myHolder.textTitle.setText(current.title);
        myHolder.textPeriod.setText(current.promotional_period);
        myHolder.viewCount.setText("Number of Views: " + current.viewCount);
        //myHolder.textPeriod.setText(ContextCompat.getColor(context,R.color.colorAccent));

        //load image into imageview using glide
        Glide.with(context).load(current.promotion_image)
                .placeholder(R.drawable.launcher_logo)
                .error(R.drawable.launcher_logo)
                .into(myHolder.ivImage);
    }

    @Override
    //return total item from list
    public int getItemCount(){
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView textName;
        TextView textTitle;
        TextView textPeriod;
        ImageView ivImage;
        TextView viewCount;

        //create constructor to get widget reference
        public MyHolder(View itemView){
            super(itemView);
            textName = (TextView)itemView.findViewById(R.id.company_name);
            ivImage = (ImageView)itemView.findViewById(R.id.promotion_image);
            textTitle = (TextView)itemView.findViewById(R.id.title);
            textPeriod = (TextView)itemView.findViewById(R.id.promotional_period);
            viewCount = (TextView)itemView.findViewById(R.id.viewCount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WarehouseSalesDetails wsd = data.get(getAdapterPosition());
                    String pid = wsd.id;
                    Intent in = new Intent(context,RetrieveIndividualWarehouseSales.class);
                    in.putExtra("pid",pid);
                    context.startActivity(in);
                }
            });
        }
    }
}
