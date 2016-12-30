package com.example.user.mycouponcodes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> stringArrayList;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)  ;
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setData();
        adapter = new ListViewAdapter(this, R.layout.item_listview, stringArrayList);
        listView.setAdapter(adapter);

    }

    private void setData(){
        stringArrayList = new ArrayList<>();
        stringArrayList.add("Chee Cheong Fun");
        stringArrayList.add("Lo Mai Kai");
        stringArrayList.add("Ha Gao");
        stringArrayList.add("Siew Mai");
        stringArrayList.add("Yao Char Kwai");
        stringArrayList.add("Bak Gor Yi Mai");
        stringArrayList.add("Red Bean");
        stringArrayList.add("Egg Tart");
        stringArrayList.add("Char Kuey Teow");
        stringArrayList.add("Pan Mee");
        stringArrayList.add("Chu Yuk Fan");
    }
}
