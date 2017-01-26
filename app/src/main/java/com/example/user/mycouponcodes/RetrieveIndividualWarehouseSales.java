package com.example.user.mycouponcodes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by User on 1/9/2017.
 */

public class RetrieveIndividualWarehouseSales extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    String title_maps;
    String promotion_period;
    String salesLocation;
    Float latitude;
    Float longitude;
    SupportMapFragment fm;
    private ShareActionProvider mShareActionProvider;
    protected BottomSheetLayout bottomSheetLayout;
    Toolbar myToolbar;
    String userNameText;
    String userEmailText;
    String userCommentText;
    String sales_id;


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
        Button postComment = (Button)findViewById(R.id.postCommentButton);
        postComment.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder alert = new AlertDialog.Builder(RetrieveIndividualWarehouseSales.this,R.style.AlertDialogTheme);
                LinearLayout layout = new LinearLayout(RetrieveIndividualWarehouseSales.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText userName = new EditText(RetrieveIndividualWarehouseSales.this);
                userName.setHint("Enter your username");
                userName.setTextColor(ContextCompat.getColor(RetrieveIndividualWarehouseSales.this,R.color.textColorPrimary));
                userName.setSingleLine(true);
                userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.account_settings_colour, 0, 0, 0);
                layout.addView(userName);
                final EditText userEmail = new EditText(RetrieveIndividualWarehouseSales.this);
                userEmail.setHint("Enter your email");
                userEmail.setTextColor(ContextCompat.getColor(RetrieveIndividualWarehouseSales.this,R.color.textColorPrimary));
                userEmail.setSingleLine(true);
                userEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_outline_colour, 0, 0, 0);
                layout.addView(userEmail);
                final EditText userComment = new EditText(RetrieveIndividualWarehouseSales.this);
                userComment.setTextColor(ContextCompat.getColor(RetrieveIndividualWarehouseSales.this,R.color.textColorPrimary));
                userComment.setHint("Write a comment...");
                layout.addView(userComment);
                alert.setTitle("Write a comment");
                alert.setView(layout);
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userNameText = userName.getText().toString();
                        userEmailText = userEmail.getText().toString();
                        userCommentText = userComment.getText().toString();
                        //whatever you want to do with the text
                        if(Patterns.EMAIL_ADDRESS.matcher(userEmail.getText()).matches()){
                            //Toast.makeText(getApplicationContext(), "Valid Email",Toast.LENGTH_LONG).show();
                            new PostComments().execute();
                        }else{
                            Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //whatever you want to do with the no button
                    }
                });
                alert.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                //Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT).show();
                bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = title_maps + " from " + promotion_period + " @" + salesLocation
                                + ". Download now at Google Play Store to stay up to date with the latest sales!";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Subject");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(RetrieveIndividualWarehouseSales.this,shareIntent,"Share via",  new IntentPickerSheetView.OnIntentPickedListener(){
                    @Override
                    public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo){
                        bottomSheetLayout.dismissSheet();
                        startActivity(activityInfo.getConcreteIntent(shareIntent));
                    }
                });
                bottomSheetLayout.showWithSheetView(intentPickerSheet);
                break;
        }
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

    }




    class RetrieveItem extends AsyncTask<Void,Void,Void>{
        private ProgressDialog pDialog;
        private String url = "http://hermosa.com.my/khlim/retrieve_individual_warehouse_sales.php";
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
                        wsd.id = s.getString("id");
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
            sales_id = wsd.id;
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
            promotion_period = wsd.promotional_period;
            salesLocation = wsd.sales_location;
            fm.getMapAsync(RetrieveIndividualWarehouseSales.this);
            setSupportActionBar(myToolbar);
        }
    }
    //to create user
    class PostComments extends AsyncTask<Void,Void,Void>{
        private ProgressDialog pDialog;
        private String url = "http://hermosa.com.my/khlim/post_comment.php";
        private String TAG = RetrieveIndividualWarehouseSales.RetrieveItem.class.getSimpleName();
        String jsonStrUserCreation;
        String userMessage = "empty";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(RetrieveIndividualWarehouseSales.this);
            pDialog.setMessage("Posting comment...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
                String urlParameters = "id="+sales_id+"&user_name="+userNameText+"&user_email="+userEmailText+"&user_comment="+userCommentText;
                System.out.println("sales_id: " + sales_id);
                System.out.println("userNameText: " + userNameText);
                System.out.println("userEmailText: " + userEmailText);
                System.out.println("userCommentText: " + userCommentText);
                jsonStrUserCreation = sh.postComment(url,urlParameters);

            Log.e(TAG, "Response from userCreationURL: " + jsonStrUserCreation);

            try{
                JSONObject jsonObj = new JSONObject(jsonStrUserCreation);
                JSONArray userDetails = jsonObj.getJSONArray("Result");
                for(int i = 0; i<userDetails.length();i++){
                    JSONObject s = userDetails.getJSONObject(i);
                    UserDetails ud = new UserDetails();
                    ud.successID = s.getString("success");
                    ud.successMessage = s.getString("message");
                    userMessage = ud.successMessage;
                    //Toast.makeText(RetrieveIndividualWarehouseSales.this, ud.successMessage, Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(RetrieveIndividualWarehouseSales.this, userMessage, Toast.LENGTH_LONG).show();
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
        }

    }
}
