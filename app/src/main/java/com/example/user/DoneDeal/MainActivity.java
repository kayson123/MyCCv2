package com.example.user.DoneDeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static ViewPager viewPager;
    private static TabLayout tabLayout;
    Spinner dropdown;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)  ;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Add a dropdown menu to toolbar
        dropdown = (Spinner)findViewById(R.id.spinner_nav);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Menu,R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(Adapter.NO_SELECTION, false);
        dropdown.setOnItemSelectedListener(new StartNewActivity());
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager); // setting tab over viewpager
        //Implementing tab selected listener over tablayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        Log.e("TAG","Tab 1");
                        break;
                    case 1:
                        Log.e("TAG","Tab 2");
                        break;
                    case 2:
                        Log.e("TAG", "Tab 3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }


            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });



    }

    public class StartNewActivity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long id) {
            String selection = ((TextView) arg1).getText().toString();
            if (selection.equals("Home")) {
                Intent intent = new Intent(arg1.getContext(), MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(arg1.getContext(), ContactUs.class);
                startActivity(intent);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    //Setting viewPager
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ActiveWarehouseSalesFragment("Active"),"Active");
        adapter.addFrag(new ExpiredWarehouseSalesFragment("Expired"),"Expired");
        viewPager.setAdapter(adapter);
    }

        //View Pager fragments setting adapter class
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>(); //fragment array list
        private final List<String> mFragmentTitleList = new ArrayList<>();//title array list

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position){
            return mFragmentList.get(position);
        }

        @Override
        public int getCount(){
            return mFragmentList.size();
        }

        //adding fragments and title method
        public void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }




}
