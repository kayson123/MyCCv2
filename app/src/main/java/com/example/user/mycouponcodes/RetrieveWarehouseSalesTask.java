package com.example.user.mycouponcodes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 1/1/2017.
 */







public class RetrieveWarehouseSalesTask extends AsyncTask<Void,Void,Void>{
        private String TAG = RetrieveWarehouseSalesTask.class.getSimpleName();
        private ListView lv;
        public ProgressDialog pDialog;
        private Context context;
        //URL to get JSON details
        private static String url = "http://192.168.0.6/mycc/retrieve_ws.php";
        ArrayList<HashMap<String,String>> sales_details;

        //for recycler view
        private RecyclerView warehouse_recycler;
        private AdapterRecycler mAdapter;
        List<WarehouseSalesDetails> data = new ArrayList<>();

        public RetrieveWarehouseSalesTask(Context context){
            this.context = context;
            sales_details = new ArrayList<>();

        }



        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
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
                        WarehouseSalesDetails wsd = new WarehouseSalesDetails();
                        wsd.company_name = s.getString("company_name");
                        wsd.promotion_image= s.getString("promotion_image");
                        wsd.title = s.getString("title");
                        wsd.promotional_period = s.getString("promotional_period");
                        data.add(wsd);

                        //HashMap<String,String> salesDetails = new HashMap<>();

                        //adding each child node to HashMap key =>value
                        /*salesDetails.put("company_name",wsd.company_name);
                        salesDetails.put("promotion_image",wsd.promotion_image);
                        salesDetails.put("title",wsd.title);
                        salesDetails.put("promotional_period",wsd.promotional_period);

                        //adding to array list
                        sales_details.add(salesDetails);*/
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

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }


            //update parsed JSON data into ListView
            /*ListAdapter adapter = new SimpleAdapter(context, sales_details,R.layout.item_listview, new String[]{
                    "company_name","promotion_image","title","promotional_period"},
                    new int[]{R.id.company_name,R.id.promotion_image,R.id.title,R.id.promotional_period});
            lv = (ListView)((AppCompatActivity) context).findViewById(R.id.list_item);

            lv.setAdapter(adapter); */

            //update RecyclerView
            warehouse_recycler = (RecyclerView)((AppCompatActivity) context).findViewById(R.id.warehouseList);
            mAdapter = new AdapterRecycler(context, data);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            warehouse_recycler.setLayoutManager(layoutManager);
            warehouse_recycler.setAdapter(mAdapter);
            warehouse_recycler.addOnItemTouchListener(
                    new RecyclerItemClickListener(context,warehouse_recycler,new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(context, "Successful click",
                            Toast.LENGTH_SHORT).show();
                }
                        @Override
                        public void onLongItemClick(View view, int position){
                            //do whatever
                        }
            }));
        }
}



