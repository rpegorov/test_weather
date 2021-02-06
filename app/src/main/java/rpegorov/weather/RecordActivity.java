package rpegorov.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static rpegorov.weather.R.layout.activity_record;

/**
 * Класс записи и воспроизведения записанного звука
 */
public class RecordActivity extends AppCompatActivity {
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    AudioRecord audioRecord;
    Random random;
    String RandomAudioFileName = "QWERTY";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_record);
        buttonStart = findViewById(R.id.record);
        buttonStop = findViewById(R.id.stop);
        buttonStopPlayingRecording = findViewById(R.id.stop_play_rec);
        buttonPlayLastRecordAudio = findViewById(R.id.play);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, RequestPermissionCode);

/** Метод инициазирует запись звука по клику кнопки
 * @AudioSavePathInDevice - указывает путь созранения и методом
 * @CreateRandomAudioFileName генерирует случайное имя файла
 */
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRec.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(RecordActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

            }
        });
/** Метод остановки записи звука по клику кнопки*/
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(RecordActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });
/** Метод проигрывания последнего файла
 * @setDataSource указывает на файл для проигрывания
 * */
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                MediaPlayerReady();

                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(RecordActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });
/** Реализация остановки проигрывания файла по клику*/
        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    /**Реализация класса  MediaRecorder
     * @AudioSource указывает на источник записи
     * @OutputFormat устанавливает исходящий формат
     * @AudioEncoder выбор кодека кодирования
     * @setOutputFile расположение исходящего файла
     * @ NoiseSuppressor.isAvailable() включение шумоподавление
     * @getAudioSessionId выбор сессии шумоподавления
     */
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        NoiseSuppressor.isAvailable();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        NoiseSuppressor.create(audioRecord.getAudioSessionId());
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  // THREE_GPP
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  // AMR_NB
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    /**
     * Реализация класса MediaPlayer
     *
     * @AudioAttributes установка контекста контента
     * @AudioAttributes установка типа контента
     * Может быть этот метод и не нужен, надо проверять...
     */
    public void MediaPlayerReady() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    /**
     * Метод создания рандомного имени файла
     */
    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    /**
     * requestPermission
     * onRequestPermissionsResult
     * запрос разрешение на чтение, запись, доступ к памяти
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(RecordActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RecordActivity.this, "Permission Denied",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    /**
     * Метод проверки разрешений
     */
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }
}
