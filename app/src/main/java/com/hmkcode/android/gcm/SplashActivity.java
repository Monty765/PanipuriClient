package com.hmkcode.android.gcm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by mahantesh on 5/30/2015.
 */
public class SplashActivity extends Activity {
    private ProgressDialog pDialog;
    SharedPreferences settings;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    GoogleCloudMessaging gcm;
    // url to get all products list
    static final String REGISTRATIONCHECK = "http://www.varaimaa.org/gcm/CheckReg.php";
    String PROJECT_NUMBER = "1081249655269";
    String real_url;
    // JSON Node names
    private static final String TAG_MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
Integer i=0;
    EditText message;
    Button btnRegId;
    String pid;
    String regid;

    // products JSONArray
    JSONArray products = null;
    TextView setname;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashactivity);
        CheckId();


    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    public void CheckId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

                    }
                    if(isConnected()){
                        Log.i("GCM", "You are conncted");
                    }
                    else{
                        Log.i("GCM", "You are not conncted");
                    }
                    String serverUrl = REGISTRATIONCHECK;
                    regid = gcm.register(PROJECT_NUMBER);
                    Map<String, String> paramss = new HashMap<String, String>();
                    paramss.put("RegId", regid);
                    post(serverUrl, paramss);
                    String json = jParser.makeServiceCall(serverUrl, JSONParser.GET);
                    // (Integer.valueOf(json).equals(0)){
                        Intent intent =new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                    //}
                   // else {
                   //     Intent intent =new Intent(SplashActivity.this, HomeActivity.class);
                    //    startActivity(intent);
                    //}

                } catch (IOException ex) {
                    msg = "Errjjorip :" + ex.getMessage();

                }
                return msg;
            }

        }.execute(null, null, null);
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
