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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.UUID;

import static android.Manifest.permission.*;
import static rpegorov.weather.R.layout.activity_record;

/**
 * Класс записи и воспроизведения записанного звука
 */
public class RecordActivity extends AppCompatActivity {
    private Button buttonStart, buttonStop, buttonPlayLastRecordAudio, buttonStopPlayingRecording;
    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder;
    public AudioRecord audioRecord;
    private static final int RequestPermissionCode = 1;
    private MediaPlayer mediaPlayer;

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

        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, RequestPermissionCode);

        buttonStart.setOnClickListener(createStartRecordClickListener());
        buttonStop.setOnClickListener(createStopClickListener());
        buttonPlayLastRecordAudio.setOnClickListener(createPlayLastRecordAudioListener());
        buttonStopPlayingRecording.setOnClickListener(createStopPlayingRecording());
    }

    private View.OnClickListener createStartRecordClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    UUID.randomUUID().toString() + "AudioRec.3gp";
                    prepareMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        buttonStart.setEnabled(false);
                        buttonStop.setEnabled(true);
                        Toast.makeText(RecordActivity.this, "Recording started", Toast.LENGTH_LONG).show();
                    } catch (IllegalStateException | IOException e) {
                        Toast.makeText(RecordActivity.this, "Error on Start records", Toast.LENGTH_LONG).show();
                    }
                } else {
                    requestPermission();
                }
            }
        };
    }

    private View.OnClickListener createStopClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                Toast.makeText(RecordActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
            }
        };
    }

    private View.OnClickListener createPlayLastRecordAudioListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);
                mediaPlayer = new MediaPlayer();
                prepareMediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(RecordActivity.this, "Recording Playing", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(RecordActivity.this, "Error on Play last Audio", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private View.OnClickListener createStopPlayingRecording() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    prepareMediaRecorder();
                }
            }
        };
    }

    /**
     * Реализация класса  MediaRecorder
     * AudioSource указывает на источник записи
     * OutputFormat устанавливает исходящий формат
     * AudioEncoder выбор кодека кодирования
     * setOutputFile расположение исходящего файла
     * NoiseSuppressor.isAvailable() проверка на наличие поддержки шумоподавления
     * getAudioSessionId выбор сессии шумоподавления
     */
    private void prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        if (NoiseSuppressor.isAvailable()) {
            NoiseSuppressor.create(audioRecord.getAudioSessionId());
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    /**
     * Реализация класса MediaPlayer
     * AudioAttributes установка контекста контента
     * AudioAttributes установка типа контента
     */
    private void prepareMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean recordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                String msg = storagePermission && recordPermission ? "Permission Granted" : "Permission Denied";
                Toast.makeText(RecordActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }
}
