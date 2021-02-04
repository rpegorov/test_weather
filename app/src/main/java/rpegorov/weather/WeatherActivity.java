package rpegorov.weather;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherActivity extends AppCompatActivity {

    TextView maxLayout, minLayout,
            viewMin, viewMax, citySearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        AndroidNetworking.initialize(getApplicationContext());
        maxLayout = findViewById(R.id.max_temp);
        minLayout = findViewById(R.id.min_temp);
        viewMax = findViewById(R.id.view_max_temp);
        viewMin = findViewById(R.id.view_min_tem);
        citySearch = findViewById(R.id.search_bar);
    }

    public void updateDataWeather(View view) {
        getWeatherData(citySearch.getText().toString());
    }

    public void getWeatherData(String cityName) {
        AndroidNetworking.get(getResources().getString(R.string.api_open_weather_map_url))
                .addQueryParameter(getResources().getString(R.string.city_key), cityName)
                .addQueryParameter(getResources().getString(R.string.metric), "metric")
                .addQueryParameter(getResources().getString(R.string.api_key), getResources().getString(R.string.appid_key_value))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            viewMax.setText(String.valueOf(response.getJSONObject("main").getDouble("temp_max")) + " ℃");
                            viewMin.setText(String.valueOf(response.getJSONObject("main").getDouble("temp_min")) + " ℃");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println(R.string.not_found);
                    }
                });

    }
}
