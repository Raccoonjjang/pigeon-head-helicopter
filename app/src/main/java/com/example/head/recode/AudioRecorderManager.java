// AudioRecorderManager.java
package com.example.head.recode;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.content.Context;
import android.net.Uri; // Add this import statement
import android.util.Log;
import android.util.Base64;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
public class AudioRecorderManager {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private AudioRecord recorder;
    private Thread recordingThread;
    private boolean isRecording = false;
    private FileManager fileManager;

    public AudioRecorderManager(Context context) {
        fileManager = new FileManager(context);
    }

    public void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        recorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(() -> {
            writeAudioDataToFile();
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte[] data = new byte[BUFFER_SIZE];
        OutputStream os = null;

        try {
            os = fileManager.createOutputStream();
            while (isRecording) {
                int read = recorder.read(data, 0, BUFFER_SIZE);
                if (read != AudioRecord.ERROR_INVALID_OPERATION && os != null) {
                    os.write(data, 0, read);
                    Log.d("AudioRecorderManager", "Writing audio data to file");
                }
            }
        } catch (IOException e) {
            Log.e("AudioRecorderManager", "Error writing audio data to file", e);
            e.printStackTrace();
        } finally {
            Uri fileUri = fileManager.getCurrentFileUri();
            fileManager.closeStreamQuietly(os, fileUri);
            Log.d("AudioRecorderManager", "Recording stopped, file saved: " + fileUri.toString());
        }
    }


    public void stopRecording() {
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;

            // 녹음 종료 후 파일을 Base64로 인코딩
            String encodedFile = encodeFileToBase64();
            Log.d("AudioRecorderManager", "인코딩된 파일: " + encodedFile);
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String encodeFileToBase64() {
        Uri fileUri = fileManager.getCurrentFileUri();
        try (InputStream inputStream = fileManager.getContext().getContentResolver().openInputStream(fileUri)) {
            if (inputStream == null) return null;
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("AudioRecorderManager", "파일을 Base64로 인코딩하는 중 오류 발생", e);
            return null;
        }
    }
}
