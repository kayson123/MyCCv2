package com.example.user.mycouponcodes;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

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
        //myHolder.textPeriod.setText(ContextCompat.getColor(context,R.color.colorAccent));

        //load image into imageview using glide
        Glide.with(context).load(current.promotion_image)
                .placeholder(R.drawable.error)
                .error(R.drawable.error)
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

        //create constructor to get widget reference
        public MyHolder(View itemView){
            super(itemView);
            textName = (TextView)itemView.findViewById(R.id.company_name);
            ivImage = (ImageView)itemView.findViewById(R.id.promotion_image);
            textTitle = (TextView)itemView.findViewById(R.id.title);
            textPeriod = (TextView)itemView.findViewById(R.id.promotional_period);
        }
    }
}
