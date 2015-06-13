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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by mahantesh on 5/30/2015.
 */
public class ChatActivity extends ListActivity  {
    private ProgressDialog pDialog;
    SharedPreferences settings;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    ListAdapter adapter;
    // url to get all products list
    private static String url_all_products = "http://www.varaimaa.org/gcm/get_messages.php";
    static final String SERVER_URL = "http://www.varaimaa.org/gcm/send_message.php";
    String real_url;
    // JSON Node names
    public static final String MY_PREFS_NAME = "texterdata";
    private static final String TAG_MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String REGIID = "MyPrefsFile";
  String RegId;
EditText message;
    Button btnRegId;
    String pid;
    // products JSONArray
    JSONArray products = null;
TextView setname;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatactivity);
        SharedPreferences shared = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences reg = getSharedPreferences(REGIID, MODE_PRIVATE);
        String name = (shared.getString("name", ""));
        pid = (shared.getString("pid", ""));
        RegId=(reg.getString("MyRegId", ""));
        Log.i("puri",pid);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String imgSett = prefs.getString("RegId", "");
        //Log.i("Pani", imgSett);
        message = (EditText) findViewById(R.id.message);
        btnRegId = (Button) findViewById(R.id.button);
        setname=(TextView)findViewById(R.id.usename);
        setname.setText(name);
        //btnRegId.setOnClickListener();
        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();
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
                String pido = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                Toast.makeText(ChatActivity.this, "Item with id "+pid , Toast.LENGTH_SHORT).show();

            };
        });
        btnRegId = (Button) findViewById(R.id.button);


    }
    public void ondd(View v) {
        String name = ((TextView) v.findViewById(R.id.name)).getText().toString();
        Toast.makeText(ChatActivity.this, "Item with id "+name , Toast.LENGTH_SHORT).show();
    }
    public void onClick(View v) {
        SendMessage();
    }
    public void SendMessage(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    String serverUrl = SERVER_URL;
                    Map<String, String> par = new HashMap<String, String>();
                    par.put("message", message.getText().toString());
                    par.put("sender", RegId);
                    par.put("reciever", pid);
                    post(serverUrl, par);
                    Intent i= new Intent(ChatActivity.this,ChatActivity.class);
                    startActivity(i);
                } catch (IOException ex) {
                    msg = "Errjjorip :" + ex.getMessage();
                }
                return msg;
            }

        }.execute(null, null, null);
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
            Intent intent =new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }
    public void onAdd(View v){
        RelativeLayout rl = (RelativeLayout)v.getParent();
        TextView tv = (TextView)rl.findViewById(R.id.name);
        String text = tv.getText().toString();
        Toast.makeText(ChatActivity.this, text, Toast.LENGTH_SHORT).show();
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
            pDialog = new ProgressDialog(ChatActivity.this);
            pDialog.setMessage("Loading messages. Please wait...");
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
            SharedPreferences reg = getSharedPreferences(REGIID, MODE_PRIVATE);
            String Regidg=(reg.getString("MyRegId",""));
            real_url=url_all_products+"?sender="+Regidg+"&reciever="+pid;
            Log.i("uerrrl", real_url);
            String json = jParser.makeServiceCall(url_all_products, JSONParser.GET);
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
            try {

                    // products found
                    // Getting Array of Products
                    JSONObject jsonObj = new JSONObject(json);

                    // Getting JSON Array node
                    products = jsonObj.getJSONArray(TAG_MESSAGES);


                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable

                        String id = c.getString(TIMESTAMP);

                        String name = c.getString(MESSAGE);
Log.i("id",id);
                        Log.i("name",name);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(MESSAGE, id);
                        map.put(TIMESTAMP, name);
                        Log.d("All Products: ", MESSAGE);
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
                    adapter = new SimpleAdapter(
                            ChatActivity.this, productsList,
                            R.layout.chat_item, new String[] {MESSAGE,TIMESTAMP},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.i("Posting",body);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {

            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }


    }

}
