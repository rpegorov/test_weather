package rpegorov.weather;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import org.json.JSONException;
import org.json.JSONObject;

import static rpegorov.weather.R.layout.activity_weather;

/**
 * Клас для получения данных о погоде на текущий момент
 */

public class WeatherActivity extends AppCompatActivity {

    TextView maxLayout, minLayout,
            viewMin, viewMax, citySearch;

    /**
     * Вызывается при входе в "полноценное состояние" инициализирует активити (activity_weather)
     * AndroidNetworking - включает запросы на сервер и извлечение возвращенных данных
     * findViewById - позволяет получить ссылку на View и инициализировать объект
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_weather);
        AndroidNetworking.initialize(getApplicationContext());
        maxLayout = findViewById(R.id.max_temp);
        minLayout = findViewById(R.id.min_temp);
        viewMax = findViewById(R.id.view_max_temp);
        viewMin = findViewById(R.id.view_min_tem);
        citySearch = findViewById(R.id.search_bar);
    }

    /**
     * В данном методе инициализируем метод getWeatherData и передаем в него параметры (название города)
     * введеные пользователем в search_bar
     */
    public void updateDataWeather(View view) {
        getWeatherData(citySearch.getText().toString());
    }

    /**
     * С помощью AndroidNetworking делаем запрос на url и передаем параметры (addQueryParameter)
     * методом getAsJSONObject получаем JSON который преобразуем объект
     */
    private void getWeatherData(String cityName) {
        AndroidNetworking.get(getResources().getString(R.string.api_open_weather_map_url))
                .addQueryParameter(getResources().getString(R.string.city_key), cityName)
                .addQueryParameter(getResources().getString(R.string.metric), getResources().getString(R.string.metric_value))
                .addQueryParameter(getResources().getString(R.string.api_key), getResources().getString(R.string.appid_key_value))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    /** onResponse используется для чтения данных из объекта JSON JSONObject response
                     * методом setText в объект (поле) viewMax записываем данные из метода getJSONObject
                     * getJSONObject - извлекает в объекте JSON параметр "main" из него извлекаем парамтер "temp_max" */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            viewMax.setText(String.valueOf(response.getJSONObject("main").getDouble("temp_max")));
                            viewMin.setText(String.valueOf(response.getJSONObject("main").getDouble("temp_min")));
                        } catch (JSONException e) {
                            Toast.makeText(WeatherActivity.this, getResources().getString(R.string.not_found), Toast.LENGTH_LONG).show();
                        }
                    }

                    /** В случае какой либо ошибки вывыести сообщение из строки */
                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(WeatherActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
