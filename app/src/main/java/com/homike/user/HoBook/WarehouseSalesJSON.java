package com.homike.user.HoBook;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by User on 3/2/2017.
 */
public class WarehouseSalesJSON {
    List<String> sales_location = new ArrayList<String>();
    //to remove duplicates
    Set<String> hashSet = new HashSet<>();

    class RetrieveWarehouseSalesJSON extends AsyncTask<Void, Void, Void> {
        private String TAG = RetrieveWarehouseSalesJSON.class.getSimpleName();
        private String TAG_PID = "pid";
        public ProgressDialog pDialog;
        private Context context;
        //URL to get JSON details
        private String url = "http://www.hermosa.com.my/khlim/retrieve_ws_production.php";
        ArrayList<HashMap<String, String>> sales_details;
        JSONObject jsonObj;
        String jsonStr;
        JSONArray sales;

        //for recycler view
        List<WarehouseSalesDetails> data = new ArrayList<>();

        public RetrieveWarehouseSalesJSON(Context context) {
            this.context = context;
            sales_details = new ArrayList<>();

        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //making a request to URL and getting response
            jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url WarehouseSalesJSON: " + jsonStr);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);
                    //Getting JSON Array Node
                    sales = jsonObj.getJSONArray("Result");
                    //looping through all results
                    for (int i = sales.length() - 1; i >= 0; i--) {
                        JSONObject s = sales.getJSONObject(i);
                        WarehouseSalesDetails wsd = new WarehouseSalesDetails();
                        wsd.expiry_date = s.getString("expiry_date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date actual_date = sdf.parse(wsd.expiry_date);
                        if(new Date().before(actual_date)){
                            wsd.id = s.getString("id");
                            wsd.company_name = s.getString("company_name");
                            wsd.promotion_image = s.getString("promotion_image");
                            wsd.title = s.getString("title");
                            wsd.promotional_period = s.getString("promotional_period");
                            wsd.viewCount = s.getString("view_count");
                            data.add(wsd);
                            sales_location.add(s.getString("state"));
                        }


                    }
                    Log.d("TAG", sales_details.toString());
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get json from server");
            }
            hashSet.addAll(sales_location);
            sales_location.clear();
            sales_location.addAll(hashSet);
            System.out.println("Array is: " + Arrays.toString(sales_location.toArray()));
            Collections.sort(sales_location, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
        }

    }
}
