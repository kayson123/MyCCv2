package com.homike.user.HoBook;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 1/5/2017.
 */

public class ActiveWarehouseSalesFragment extends Fragment {
    private View view;
    private String title; //String for tab title
    private RecyclerView recyclerView;
    private PullToRefreshView mPullToRefreshView;




    public ActiveWarehouseSalesFragment(){

    }

    public ActiveWarehouseSalesFragment(String title) {
        this.title = title;//Setting tab title
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.active_warehouse_sales_layout, container, false);
        new RetrieveWarehouseSalesTask(getActivity()).execute();
        mPullToRefreshView = (PullToRefreshView)view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new RetrieveWarehouseSalesTask(getActivity()).execute();
                    }
                }, 10);
            }
        });

        return view;
    }

    class RetrieveWarehouseSalesTask extends AsyncTask<Void,Void,Void> {
        private String TAG = RetrieveWarehouseSalesTask.class.getSimpleName();
        private String TAG_PID = "pid";
        public ProgressDialog pDialog;
        private Context context;
        //URL to get JSON details
        private String url = "http://www.hermosa.com.my/khlim/retrieve_ws_production.php";
        ArrayList<HashMap<String,String>> sales_details;
        JSONObject jsonObj;
        String jsonStr;
        JSONArray sales;
        String filteredLocation = MainActivity.selectedLocation;


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
            System.out.println("FilteredLocation is: " + filteredLocation);
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            //making a request to URL and getting response
            jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            if(jsonStr != null){
                try{
                    jsonObj = new JSONObject(jsonStr);
                    //Getting JSON Array Node
                    sales = jsonObj.getJSONArray("Result");
                    //looping through all results
                    for(int i = sales.length() - 1; i >= 0 ;i--){
                        JSONObject s = sales.getJSONObject(i);
                        WarehouseSalesDetails wsd = new WarehouseSalesDetails();
                        wsd.expiry_date = s.getString("expiry_date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date actual_date = sdf.parse(wsd.expiry_date);
                        if(filteredLocation == null || filteredLocation.equals("All") ) {
                            if (new Date().before(actual_date)) {
                                wsd.id = s.getString("id");
                                wsd.company_name = s.getString("company_name");
                                wsd.promotion_image = s.getString("promotion_image");
                                wsd.title = s.getString("title");
                                wsd.promotional_period = s.getString("promotional_period");
                                wsd.viewCount = s.getString("view_count");
                                data.add(wsd);
                            }
                        }else if(filteredLocation.equals(s.getString("state")) && new Date().before(actual_date)){
                            wsd.id = s.getString("id");
                            wsd.company_name = s.getString("company_name");
                            wsd.promotion_image = s.getString("promotion_image");
                            wsd.title = s.getString("title");
                            wsd.promotional_period = s.getString("promotional_period");
                            wsd.viewCount = s.getString("view_count");
                            data.add(wsd);
                        }
                    }
                    Log.d("TAG",sales_details.toString());
                }catch(final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Log.e(TAG,"Couldn't get json from server");
            }
            //update RecyclerView
            warehouse_recycler = (RecyclerView)((AppCompatActivity) context).findViewById(R.id.recyclerView);
            mAdapter = new AdapterRecycler(context, data);
            System.out.println("mAdapter size is: " + mAdapter.getItemCount());
            System.out.println("Data size is: " + data.size());
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            warehouse_recycler.setLayoutManager(layoutManager);
            //mAdapter.notifyDataSetChanged();
            warehouse_recycler.setAdapter(mAdapter);
            warehouse_recycler.invalidate();
            mPullToRefreshView.setRefreshing(false);
        }

    }



}
