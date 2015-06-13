package com.hmkcode.android.gcm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends Activity implements OnClickListener {
    JSONParser jParser = new JSONParser();
    Button btnRegId;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid;
    EditText username;
    EditText email;
    String ProfileType="non";
    static final String REGISTRATIONCHECK = "http://www.varaimaa.org/gcm/CheckReg.php";
    String PROJECT_NUMBER = "1081249655269";
    static final String SERVER_URL = "http://www.varaimaa.org/gcm/register.php";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        radioSexGroup = (RadioGroup) findViewById(R.id.ptype);
        username = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.email);
        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        btnRegId.setOnClickListener(this);
    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
//APA91bF06gXkKnwmyPV8WWhsvhsKBU8c2B4j8_GwmWFHvSLs-slxmiaMHx01I_VLIFarJIi9pvFZzCC_PlNUeF6XzWN9TSmWqKxuJ06u9GglKj_EMjcvyzDsHgaGtND6NdRCu0SDdviREGBJ39acYJCQrLeUSF1VHA
    public void getRegId(){
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
                    int selectedId = radioSexGroup.getCheckedRadioButtonId();
                    radioSexButton = (RadioButton) findViewById(selectedId);
                    String serverUrl = SERVER_URL;
                    Map<String, String> par = new HashMap<String, String>();
                    regid = gcm.register(PROJECT_NUMBER);
                    par.put("regId", regid);
                    par.put("name", username.getText().toString());
                    par.put("email", email.getText().toString());
                    par.put("ptype" +
                            "", radioSexButton.getText().toString());
                    post(serverUrl, par);
                    msg = "Device registered, registration ID=" + regid;
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("MyRegId", regid);
                    editor.commit();
                    Log.i("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Errjjorip :" + ex.getMessage();

                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                Intent intent =new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }.execute(null, null, null);
    }



    @Override
    public void onClick(View v) {
       getRegId();

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
        Log.i("url",body);
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