package com.example.user.mycouponcodes;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity {


    /*private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;*/

    //URL to get JSON details
    //private static String url = "http://192.168.0.6/mycc/retrieve_ws.php";
    //ArrayList<HashMap<String,String>> sales_details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)  ;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Warehouse Sales");
        //sales_details = new ArrayList<>();
        //lv = (ListView)findViewById(R.id.list_item);
        //GetWarehouseSales gws = new GetWarehouseSales(MainActivity.this);
        //gws.executeGWS();
        new RetrieveWarehouseSalesTask(this).execute();


    }


    /*private class GetWarehouseSales extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting you the best warehouse sales...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            //making a request to URL and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if(jsonStr != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Getting JSON Array Node
                    JSONArray sales = jsonObj.getJSONArray("Result");
                    //looping through all results
                    for(int i = 0; i<sales.length();i++){
                        JSONObject s = sales.getJSONObject(i);
                        String title = s.getString("title");
                        String description = s.getString("description");

                        HashMap<String,String> salesDetails = new HashMap<>();

                        //adding each child node to HashMap key =>value
                        salesDetails.put("title",title);
                        salesDetails.put("description",description);

                        //adding to array list
                        sales_details.add(salesDetails);
                    }
                    Log.d("TAG",sales_details.toString());
                }catch(final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"JSON parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                Log.e(TAG,"Couldn't get json from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Check logcat", Toast.LENGTH_LONG ).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            //update parsed JSON data into ListView
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, sales_details,R.layout.item_listview, new String[]{
                    "title","description"}, new int[]{R.id.text,R.id.description});

            lv.setAdapter(adapter);
        }
    }*/

}
