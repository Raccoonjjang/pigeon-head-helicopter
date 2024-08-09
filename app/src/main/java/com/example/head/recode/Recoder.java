package com.example.head.recode;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Recoder {

    private MediaRecorder recorder = null;
    private boolean isRecording = false;
    private String fileName = null;
    private Context context;

    public Recoder(Context context) {
        this.context = context;
    }

    // 파일 경로를 매개변수로 받아서 녹음을 시작하는 메소드
    public void startRecording(String filePath) {

        fileName = filePath;  // 전달받은 파일 경로를 사용

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

            // 녹음 파일을 Base64로 인코딩
            try {
                String base64EncodedAudio = encodeAudioFileToBase64(fileName);
                // Base64로 인코딩된 오디오 데이터를 처리 (예: 서버로 전송, 로그 출력 등)
                System.out.println("Base64 Encoded Audio: " + base64EncodedAudio);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getFileName() {
        return fileName;
    }

    // 파일을 Base64로 인코딩하는 메소드
    private String encodeAudioFileToBase64(String filePath) throws IOException {
        File audioFile = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(audioFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        fileInputStream.close();
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
    }
}
