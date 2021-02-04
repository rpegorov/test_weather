package rpegorov.weather;

/** Тестовое приложение погода с записью звука
 * @author rpegorov
 * @version 1
 * @since  04.02.2021*/

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/** Класс стартовой активити, содержит методы для перехода в другие активити*/
public class MainActivity extends AppCompatActivity {
   /**  Вызывается при входе в "полноценное состояние" инициализирует активити */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
/** Методы перехода в другиеи активити */
    public void goToWeatherActivity(View view) {
        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        startActivity(intent);
    }
    public void goToRecordActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        startActivity(intent);
    }
}