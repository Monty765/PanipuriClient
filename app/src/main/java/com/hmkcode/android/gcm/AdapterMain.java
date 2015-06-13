package com.hmkcode.android.gcm;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class AdapterMain extends Activity {

    private ListView listView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adaptermain);

        Weather weather_data[] = new Weather[]
                {
                        new Weather(R.drawable.head, "Cloudy"),
                        new Weather(R.drawable.head, "Showers"),
                        new Weather(R.drawable.head, "Snow"),
                        new Weather(R.drawable.head, "Storm"),
                        new Weather(R.drawable.head, "Sunny")
                };

        WeatherAdapter adapter = new WeatherAdapter(this,
                R.layout.adapterlist, weather_data);


        listView1 = (ListView) findViewById(R.id.listView1);


        listView1.setAdapter(adapter);
    }
}