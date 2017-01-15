package com.example.user.mycouponcodes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * Created by User on 1/6/2017.
 */

public class ExpiredWarehouseSalesFragment extends Fragment {
    private View view;
    private String title; //String for tab title
    private static RecyclerView recyclerView;
    public ExpiredWarehouseSalesFragment(){

    }

    public ExpiredWarehouseSalesFragment(String title) {
        this.title = title;//Setting tab title
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.expired_warehouse_sales_layout, container, false);
        new RetrieveWarehouseSalesTask(getActivity()).execute();

        return view;
    }


    class RetrieveWarehouseSalesTask extends AsyncTask<Void,Void,Void> {
        private String TAG = RetrieveWarehouseSalesTask.class.getSimpleName();
        public ProgressDialog pDialog;
        private Context context;
        //URL to get JSON details
        private String url = "http://www.hermosa.com.my/khlim/retrieve_ws.php";
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
                        wsd.expiry_date = s.getString("expiry_date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date actual_date = sdf.parse(wsd.expiry_date);
                        if(new Date().after(actual_date)){
                            wsd.id = s.getString("id");
                            wsd.company_name = s.getString("company_name");
                            wsd.promotion_image= s.getString("promotion_image");
                            wsd.title = s.getString("title");
                            wsd.promotional_period = s.getString("promotional_period");
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }



            //update RecyclerView
            warehouse_recycler = (RecyclerView)((AppCompatActivity) context).findViewById(R.id.recyclerViewWarehouseExpired);
            mAdapter = new AdapterRecycler(context, data);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            warehouse_recycler.setLayoutManager(layoutManager);
            warehouse_recycler.setAdapter(mAdapter);
            warehouse_recycler.addOnItemTouchListener(
                    new RecyclerItemClickListener(context,warehouse_recycler,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            WarehouseSalesDetails wsd = data.get(position);
                            String pid = wsd.id;
                            Intent in = new Intent(context,RetrieveIndividualWarehouseSales.class);
                            in.putExtra("pid",pid);
                            startActivity(in);
                        }
                        @Override
                        public void onLongItemClick(View view, int position){
                            //do whatever
                        }
                    }));
        }
    }

}
