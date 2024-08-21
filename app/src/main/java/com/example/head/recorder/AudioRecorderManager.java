package com.example.head.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.head.api.Pronunciation;

import java.io.FileOutputStream;
import java.io.IOException;
public class AudioRecorderManager {
    //<ㄷ
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private int bufferSize;
    private Context context;
    private String filePath;
    private Pronunciation pronunciation;

    public AudioRecorderManager(Context context) {
        this.context = context;
        bufferSize = AudioRecord.getMinBufferSize(16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }

    public void startRecording() {

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        audioRecord.startRecording();
        isRecording = true;

        // 파일을 저장할 경로를 생성합니다.
        filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/recorded_audio.pcm";

        // 녹음 작업을 백그라운드 스레드에서 실행합니다.
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }).start();
    }

    private void writeAudioDataToFile() {
        byte[] audioData = new byte[bufferSize];
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filePath);

            while (isRecording) {
                int read = audioRecord.read(audioData, 0, bufferSize);
                if (read > 0) {
                    os.write(audioData, 0, read);
                }
            }

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(String scripts) {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            Log.d("AudioRecorderManager", "File path: " + filePath);

            // 여기서 ApiService를 호출하여 PCM 파일을 서버로 전송할 수 있습니다.
            pronunciation.sendAudioDataToApi(filePath, scripts);
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getFilePath() {
        return filePath;
    }
}