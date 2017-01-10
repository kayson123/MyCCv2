package com.example.user.mycouponcodes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;



/**
 * Created by User on 1/9/2017.
 */

public class RetrieveIndividualWarehouseSales extends Activity {


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_individual_warehouse);
        new RetrieveItem().execute();

    }

    class RetrieveItem extends AsyncTask<String,String,String>{
        private ProgressDialog pDialog;
        private String url = "http://192.168.0.6/mycc/retrieve_individual_warehouse_sales.php";
        private String TAG = RetrieveIndividualWarehouseSales.RetrieveItem.class.getSimpleName();
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(RetrieveIndividualWarehouseSales.this);
            pDialog.setMessage("Getting you the information...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(String... args){
            Intent intent = getIntent();
            String pid = intent.getStringExtra("pid");
            Log.e(TAG,"pid is: " + pid);

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeHttpRequest(url,pid);
            Log.e(TAG, "Response from url: " + jsonStr);

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}
