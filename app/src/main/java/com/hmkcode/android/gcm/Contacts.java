package com.hmkcode.android.gcm;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mahantesh on 5/30/2015.
 */
public class Contacts extends ListActivity {
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_all_products = "http://www.varaimaa.org/gcm/get_users.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    public static final String MY_PREFS_NAME = "texterdata";
    // products JSONArray
    JSONArray products = null;
    Button selected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pp);

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllProducts().execute();
        final ListView lv1 ;

        // Get listview
        lv1 = getListView();
        lv1.setClickable(true);
        // on seleting single product
        // launching Edit Product Screen
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // getting values from selected ListItem

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                Toast.makeText(Contacts.this, "Item with id "+name , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Contacts.this, ChatActivity.class);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", name);
                editor.putString("pid", pid);
                editor.commit();
                startActivity(intent);

            };
        });

    }

    public void gocontact(View v){
        Intent intent = new Intent(Contacts.this, HomeActivity.class);
        startActivity(intent);
    }
    public void gopublic(View v){
        Intent intent = new Intent(Contacts.this, Contacts.class);
        startActivity(intent);
    }
    public void onad(View view){

        String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
        Toast.makeText(Contacts.this, "Item with id " + name, Toast.LENGTH_SHORT).show();
    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }



    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Contacts.this);
            pDialog.setMessage("Loading Contacts. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }



        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL

            String json = jParser.makeServiceCall(url_all_products, JSONParser.GET);

            try {

                // products found
                // Getting Array of Products
                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                products = jsonObj.getJSONArray(TAG_PRODUCTS);


                // looping through All Products
                for (int i = 0; i < products.length(); i++) {
                    JSONObject c = products.getJSONObject(i);

                    // Storing each json item in variable
                    String id = c.getString(TAG_PID);
                    String name = c.getString(TAG_NAME);
                    Log.i("id",id);
                    Log.i("name",name);
                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_PID, id);
                    map.put(TAG_NAME, name);
                    Log.d("All Products: ", TAG_NAME);
                    // adding HashList to ArrayList
                    productsList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            Contacts.this, productsList,
                            R.layout.list_item, new String[] {TAG_PID,TAG_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}
