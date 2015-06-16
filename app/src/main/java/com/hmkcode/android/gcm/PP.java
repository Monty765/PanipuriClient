package com.hmkcode.android.gcm;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hmkcode.android.adapters.lists.AdapterMain;
import com.hmkcode.android.adapters.lists.PPlist;

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
public class PP extends ListActivity {
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<PPlist> productsList;

    // url to get all products list
    private static String url_all_products = "http://www.varaimaa.org/gcm/get_users.php";
    ArrayList<PPlist> pubnameArray = new ArrayList<PPlist>();
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    public static final String MY_PREFS_NAME = "texterdata";
    // products JSONArray
    JSONArray products = null;
    Button selected;
    AlertDialog alertDialogStores;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pp);

        // Hashmap for ListView
        productsList = new ArrayList<PPlist>();

        // Loading products in Background Thread
        new LoadAllProducts().execute();

    }



            public void gocontact(View v) {
                Intent intent = new Intent(PP.this, HomeActivity.class);
                startActivity(intent);
            }

            public void gopublic(View v) {
                Intent intent = new Intent(PP.this, Contacts.class);
                startActivity(intent);
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
                    pDialog = new ProgressDialog(PP.this);
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

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value

                            Log.d("pp All Products: ", name);
                            // adding HashList to ArrayList
                            productsList.add(new PPlist(name,id));
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
                           AdapterMain adapter = new AdapterMain(PP.this,R.layout.pub_list, productsList);
                            setListAdapter(adapter);
                            Log.d("pp All Products: ", "loaded");
                        }
                    });

                }

            }
        }
