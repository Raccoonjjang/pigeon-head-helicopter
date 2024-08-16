package com.example.head.recorder;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import com.example.head.api.ApiService;
import android.database.Cursor;
import android.provider.MediaStore;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecorderManager {
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private Uri fileUri;
    private Context context;
    private ApiService apiService;
    // 생성자에 Context를 추가합니다.
    public AudioRecorderManager(Context context) {
        this.context = context;
    }

    // 녹음을 시작하는 메소드입니다.
    public void startRecording() {
        try {
            fileUri = createFileUri(); // 녹음 파일의 URI를 생성합니다.
            setupMediaRecorder(); // MediaRecorder를 설정합니다.

            // 녹음을 준비하고 시작합니다.
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true; // 녹음 상태를 true로 변경합니다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MediaRecorder를 설정하는 메소드입니다.
    private void setupMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 오디오 소스 설정
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 출력 형식 설정
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 오디오 인코더 설정

        // ParcelFileDescriptor를 통해 얻은 FileDescriptor를 사용
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(fileUri, "w");
        if (pfd == null) {
            throw new IOException("Cannot open file descriptor for URI: " + fileUri);
        }

        mediaRecorder.setOutputFile(pfd.getFileDescriptor());
    }

    // MediaStore를 사용하여 녹음 파일의 URI를 생성하는 메소드입니다.
    private Uri createFileUri() throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, createFileName()); // 파일의 이름을 설정합니다.
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp"); // 파일의 MIME 타입을 설정합니다.
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/Head"); // 파일의 저장 경로를 설정합니다.

        // 생성된 정보를 바탕으로 파일 URI를 MediaStore에 추가합니다.
        Uri uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IOException("Failed to create new MediaStore record.");
        }
        return uri;
    }

    // 현재 시간을 기반으로 파일 이름을 생성하는 메소드입니다.
    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "REC_" + timeStamp + ".3gpp";
    }

    // 녹음을 중지하고 MediaRecorder 리소스를 해제하는 메소드입니다.
    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop(); // 녹음을 중지합니다.
            mediaRecorder.release(); // MediaRecorder 리소스를 해제합니다.
            mediaRecorder = null; // MediaRecorder 객체를 null로 설정합니다.
            isRecording = false; // 녹음 상태를 false로 변경합니다.
            Log.d("AudioRecorderManager", "File URI: " + fileUri.toString());

            // 파일 경로를 가져와서 ApiService로 전달합니다.
            String filePath = getFilePathFromUri(fileUri);
            apiService.sendAudioDataToApi(filePath);
        }
    }

    // URI에서 파일 경로를 가져오는 메소드입니다.
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] projection = { MediaStore.Audio.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
                cursor.close();
            }
        } else if ("file".equals(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath;
    }
    // 녹음 중인지 확인하는 메소드입니다.
    public boolean isRecording() {
        return isRecording;
    }

}