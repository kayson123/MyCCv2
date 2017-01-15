package com.example.user.mycouponcodes;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by User on 1/9/2017.
 */

public class RetrieveIndividualWarehouseSales extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    String title_maps;
    Float latitude;
    Float longitude;
    SupportMapFragment fm;
    private ShareActionProvider mShareActionProvider;
    Toolbar myToolbar;


    /*public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_individual_warehouse);
        new RetrieveItem().execute();
    }*/
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_individual_warehouse);
        //lets set the toolbar
        new RetrieveItem().execute();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        fm =(SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.map);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //create the sharing intent
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = title_maps + ". Download now at Google Play Store to stay up to date with the latest sales!";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Subject");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        //set the sharing intent
        mShareActionProvider.setShareIntent(sharingIntent);
        return true;
    }
    @Override
    public void onMapReady(GoogleMap gm) {
        googleMap = gm;
        if(latitude == null || longitude == null){
            getSupportFragmentManager().beginTransaction().hide(fm).commit();
        }else{
            LatLng location = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(location).title(title_maps)).showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        }

        //System.out.println("latitude from onMapReady: " + latitude);

    }




    class RetrieveItem extends AsyncTask<Void,Void,Void>{
        private ProgressDialog pDialog;
        private String url = "http://www.hermosa.com.my/khlim/retrieve_individual_warehouse_sales.php";
        private String TAG = RetrieveIndividualWarehouseSales.RetrieveItem.class.getSimpleName();
        List<WarehouseSalesDetails> data = new ArrayList<>();
        ArrayList<HashMap<String,String>> sales_details;


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(RetrieveIndividualWarehouseSales.this);
            pDialog.setMessage("Getting you the information...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0){
            sales_details = new ArrayList<>();
            Intent intent = getIntent();
            String pid = intent.getStringExtra("pid");
            Log.e(TAG,"pid is: " + pid);

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeHttpRequest(url,pid);
            Log.e(TAG, "Response from url: " + jsonStr);
            if(jsonStr != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Getting JSON Array Node
                    JSONArray sales = jsonObj.getJSONArray("Result");
                    //looping through all results
                    for(int i = 0; i<sales.length();i++){
                        JSONObject s = sales.getJSONObject(i);
                        WarehouseSalesDetails wsd = new WarehouseSalesDetails();
                        wsd.company_name = s.getString("company_name");
                        wsd.promotion_image= s.getString("promotion_image");
                        wsd.title = s.getString("title");
                        wsd.promotional_period = s.getString("promotional_period");
                        wsd.sales_description = s.getString("sales_description");
                        wsd.sales_location = s.getString("sales_location");
                        if(s.getDouble("latitude") == 0 || s.getDouble("longitude") == 0){
                            wsd.latitude = null;
                            wsd.longitude = null;
                        }else{
                            wsd.latitude = BigDecimal.valueOf(s.getDouble("latitude")).floatValue();
                            wsd.longitude = BigDecimal.valueOf(s.getDouble("longitude")).floatValue();
                        }
                        data.add(wsd);

                    }
                    Log.d("TAG",sales_details.toString());
                }catch(final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                }
            }else{
                Log.e(TAG,"Couldn't get json from server");
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            WarehouseSalesDetails wsd = data.get(0);
            String imageURL = wsd.promotion_image;
            ImageView promotional_image = (ImageView) findViewById(R.id.promotion_image);
            TextView company_name = (TextView)findViewById(R.id.company_name);
            TextView title = (TextView)findViewById(R.id.title);
            TextView promotional_period = (TextView)findViewById(R.id.promotional_period);
            //promotional_period.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_black_18dp, 0, 0, 0);
            TextView sales_location = (TextView)findViewById(R.id.sales_location);
            TextView sales_description = (TextView)findViewById(R.id.sales_description);
            company_name.setText(wsd.company_name);
            title.setText(wsd.title);
            sales_description.setText(wsd.sales_description);
            String valid_from = "Valid From: \n";
            promotional_period.setText(valid_from + wsd.promotional_period);
            sales_location.setText(wsd.sales_location);
            //load image into imageview using glide
            Glide.with(RetrieveIndividualWarehouseSales.this).load(imageURL)
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .into(promotional_image);
            //google maps
            latitude = wsd.latitude;
            longitude = wsd.longitude;
            title_maps = wsd.title;
            fm.getMapAsync(RetrieveIndividualWarehouseSales.this);
            setSupportActionBar(myToolbar);




        }
    }
}
