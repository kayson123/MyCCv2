package com.homike.user.HoBook;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v4.widget.SimpleCursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2/26/2017.
 */

public class SearchActivity extends AppCompatActivity {
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private SimpleCursorAdapter myAdapter;
    SearchView searchView = null;
    private WarehouseSalesDetails[] strArrData;
    HashMap<String, String> idMap = new HashMap<String, String>();
    String selectedTitle1;
    String selectedID;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item);
        Toolbar toolBarSearch = (Toolbar)findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolBarSearch);

        final String[] from = new String[]{"title"};
        final int[] to = new int[]{android.R.id.text1};
        //setup SimpleCursorAdapter
        myAdapter = new SimpleCursorAdapter(SearchActivity.this,android.R.layout.simple_spinner_dropdown_item,null,from,to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        new RetrieveWarehouseSalesTask(this).execute();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_filter,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager)SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if(searchItem!=null){
            searchView = (SearchView) searchItem.getActionView();
        }
        if(searchView!=null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconified(false);
            searchView.setSuggestionsAdapter(myAdapter);
            //getting selected on item suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener(){
                @Override
                public boolean onSuggestionClick(int position){
                    //Add clicked text to search box
                    CursorAdapter ca = searchView.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    //to print hashmap values
                    for (Map.Entry entry : idMap.entrySet()) {
                        System.out.println("idMap: " + entry.getKey() + ", " + entry.getValue());

                    }

                    if(cursor.moveToPosition(position)){
                        String selectedTitle = cursor.getString(cursor.getColumnIndex("title"));
                        searchView.setQuery(cursor.getString(cursor.getColumnIndex("title")),false);
                        System.out.println("Get key from value: " + getKeyFromValue(idMap,selectedTitle));
                        selectedID = getKeyFromValue(idMap,selectedTitle).toString();
                        Log.d("selected id",selectedID);
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                        Intent intent = new Intent(SearchActivity.this,RetrieveIndividualWarehouseSales.class);
                        intent.putExtra("pid",selectedID);
                        startActivity(intent);

                    }
                    searchView.clearFocus();

                    return true;

                }

                @Override
                public boolean onSuggestionSelect(int position){
                    return true;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String s){
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String s){
                    //filter data
                    final MatrixCursor mc = new MatrixCursor(new String[]{
                            BaseColumns._ID,"title"
                    });
                    for(int i = 0; i<strArrData.length; i++){
                        if(strArrData[i].title.toLowerCase().startsWith(s.toLowerCase())){
                            mc.addRow(new Object[]{i,strArrData[i].title});
                        }

                    }

                    myAdapter.changeCursor(mc);
                    return false;
                }
            });


        }
        return true;
    }

    // to get the hashmap key based on hashmap value
    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(searchView!=null){
                searchView.clearFocus();
            }
        }
    }

    class RetrieveWarehouseSalesTask extends AsyncTask<Void,Void,Void> {
        private String TAG = ActiveWarehouseSalesFragment.RetrieveWarehouseSalesTask.class.getSimpleName();
        private String TAG_PID = "pid";
        public ProgressDialog pDialog;
        private Context context;
        //URL to get JSON details
        private String url = "http://www.hermosa.com.my/khlim/retrieve_ws_production.php";
        ArrayList<HashMap<String,String>> sales_details;
        List<WarehouseSalesDetails> data = new ArrayList<>();
        JSONObject jsonObj;
        String jsonStr;
        JSONArray sales;

        //for recycler view
        private RecyclerView warehouse_recycler;
        private AdapterRecycler mAdapter;


        public RetrieveWarehouseSalesTask(Context context){
            this.context = context;
            sales_details = new ArrayList<>();

        }



        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Searching...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            //making a request to URL and getting response
            jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url (SearchActivity): " + jsonStr);


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //ArrayList<String> dataList = new ArrayList<String>();
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
                        wsd.id = s.getString("id");
                        wsd.company_name = s.getString("company_name");
                        wsd.promotion_image= s.getString("promotion_image");
                        wsd.title = s.getString("title");
                        wsd.promotional_period = s.getString("promotional_period");
                        wsd.viewCount = s.getString("view_count");
                        data.add(wsd);
                        idMap.put(s.getString("id"),s.getString("title"));
                    }
                    //strArrData = dataList.toArray(new String[dataList.size()]);
                    strArrData = data.toArray(new WarehouseSalesDetails[data.size()]);
                    Log.d("TAG",sales_details.toString());
                }catch(final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                }
            }else{
                Log.e(TAG,"Couldn't get json from server");
            }
            Intent intent1 = getIntent();
            selectedTitle1 = intent1.getStringExtra("selectedTitle");
            System.out.println("selectedTitle1: " + selectedTitle1);
            if(selectedTitle1 != null){
                selectedID = getKeyFromValue(idMap,selectedTitle1).toString();
                Log.d("selected id",selectedID);
                Intent intent = new Intent(SearchActivity.this,RetrieveIndividualWarehouseSales.class);
                intent.putExtra("pid",selectedID);
                startActivity(intent);
            }
        }

    }
}
