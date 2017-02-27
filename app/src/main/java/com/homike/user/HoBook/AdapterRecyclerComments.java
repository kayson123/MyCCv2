package com.homike.user.HoBook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by User on 1/27/2017.
 */

public class AdapterRecyclerComments extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<CommentDetails> data = Collections.emptyList();
    CommentDetails current;
    int currentPos = 0;

    //to initialize context and data sent from MainActivity
    public AdapterRecyclerComments(Context context, List<CommentDetails> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    //inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.comments_listview,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    //Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        //get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        CommentDetails current = data.get(position);
        myHolder.textName.setText(current.userComment);
        myHolder.userName.setText(current.userName);
    }

    @Override
    //return total item from list
    public int getItemCount(){
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView textName;
        TextView userName;

        //create constructor to get widget reference
        public MyHolder(View itemView){
            super(itemView);
            textName = (TextView)itemView.findViewById(R.id.comments);
            userName = (TextView)itemView.findViewById(R.id.userName);
        }
    }
}
