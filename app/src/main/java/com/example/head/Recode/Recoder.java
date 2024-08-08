package com.example.head.Recode;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.IOException;

public class Recoder {

    private MediaRecorder recorder = null;
    private boolean isRecording = false;
    private String fileName = null;
    private Context context;

    public Recoder(Context context) {
        this.context = context;
    }

    public void startRecording() {/*
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permissions are not granted", Toast.LENGTH_SHORT).show();
            return;
        }*/

        fileName = context.getExternalCacheDir().getAbsolutePath() + "/audiorecordtest.3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show();
        isRecording = true;
    }

    public void stopRecording() {
        if (recorder != null && isRecording) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show();
            isRecording = false;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
