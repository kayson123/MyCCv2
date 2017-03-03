package com.homike.user.HoBook;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.homike.user.HoBook.app.Config;
import com.homike.user.HoBook.util.NotificationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static ViewPager viewPager;
    private static TabLayout tabLayout;
    private FloatingActionButton floatSearch;
    public static String selectedLocation;

    //for firebase
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //for location filter
    List<String> warehouse_sales_location = new ArrayList<String>();
    final WarehouseSalesJSON retrieve = new WarehouseSalesJSON();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)  ;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //bottom navigation view
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId()){
                    case R.id.warehouse_sales:
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.contact_us:
                        Intent intentContact = new Intent(MainActivity.this, ContactUs.class);
                        startActivity(intentContact);
                        break;
                }

                return false;
            }
        });

        //search float
        floatSearch = (FloatingActionButton)findViewById(R.id.float_search);
        floatSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

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

        //for firebase
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Config.REGISTRATION_COMPLETE)){
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                }else if(intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    String message = intent.getStringExtra("message");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(MainActivity.this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("HoBook")
                                    .setContentText(message);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(context, SearchActivity.class);
                    resultIntent.putExtra("selectedTitle",message);
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(123, mBuilder.build());


                    System.out.println("Message is: " + message);
                    //Toast.makeText(getApplicationContext(),"Push Notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        displayFirebaseRegId();


        //for location filter
        retrieve.new RetrieveWarehouseSalesJSON(MainActivity.this).execute();
        new Thread(new Runnable(){
            public void run(){
                try{
                    Thread.sleep(500);//fake doing the job
                } catch (Exception e){
                    e.printStackTrace();
                }
                warehouse_sales_location = retrieve.sales_location;
                System.out.println("warehouse_sales_location is: " + Arrays.toString(warehouse_sales_location.toArray()));
            }
        }).start();

        Button filterButton = (Button)findViewById(R.id.filter_location);
        filterButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //reason for repitition : so that the selection gets updated. but need to click twice
                retrieve.new RetrieveWarehouseSalesJSON(MainActivity.this).execute();
                warehouse_sales_location = retrieve.sales_location;
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.radio_filter_dialog);
                RadioGroup rg = (RadioGroup)dialog.findViewById(R.id.radio_group);
                RadioButton ra = new RadioButton(MainActivity.this);
                ra.setText("All");
                rg.addView(ra);
                //custom dialog
                for(int i=0; i<warehouse_sales_location.size(); i++){
                    RadioButton rb = new RadioButton(MainActivity.this);
                    rb.setText(warehouse_sales_location.get(i));
                    rg.addView(rb);
                }
                dialog.show();
                System.out.println("warehouse_sales_location is in dialog: " + Arrays.toString(warehouse_sales_location.toArray()));
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup group,int checkedId){
                        int childCount = group.getChildCount();
                        for(int x =0 ; x < childCount; x++){
                            RadioButton btn = (RadioButton)group.getChildAt(x);
                            if(btn.getId() == checkedId){
                                Log.e("selected RadioButton->",btn.getText().toString());
                                selectedLocation = btn.getText().toString();
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,"Selected Location: " + selectedLocation,
                                        Toast.LENGTH_LONG).show();
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
                        }
                    }
                });

            }
        });



    }

    //for firebase
    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            System.out.println("Firebase Reg Id: " + regId);
        else
            System.out.println("Firebase Reg Id is not received yet!");
    }

    //for firebase
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    //for firebase
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
