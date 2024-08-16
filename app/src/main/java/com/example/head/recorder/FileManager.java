package com.example.head.recorder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {

    private Context context;
    private Uri currentFileUri;

    public FileManager(Context context) {
        this.context = context;
    }

    public OutputStream createOutputStream() throws IOException {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        String fileName = generateFileName();

        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/x-wav");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);

        currentFileUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        if (currentFileUri == null) {
            throw new IOException("Failed to create new MediaStore record.");
        }

        OutputStream os = resolver.openOutputStream(currentFileUri);
        if (os != null) {
            writeWavHeader(os);
        }

        return os;
    }

    private void writeWavHeader(OutputStream os) throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';

        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16;
        header[20] = 1;
        header[22] = 1;
        int sampleRate = 16000;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        int byteRate = sampleRate * 2;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = 2;
        header[34] = 16;

        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';

        os.write(header, 0, 44);
    }

    public void closeStreamQuietly(OutputStream os, Uri currentFileUri) {
        try {
            if (os != null) {
                os.close();
                updateWavHeader(currentFileUri);

                if (currentFileUri != null) {
                    String message = "Saving complete, URI: " + currentFileUri.toString();
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateWavHeader(Uri fileUri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            try (OutputStream os = resolver.openOutputStream(fileUri, "rwt")) {
                if (os == null) return;

                long fileSize = calculateFileSize(fileUri) - 8;
                long dataSize = fileSize - 36;

                // Jump to the 4th byte and write the file size
                os.write(intToByteArray((int) fileSize), 0, 4);
                os.flush();
                // Jump to the 40th byte and write the data size
                os.write(intToByteArray((int) dataSize), 0, 4);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 24) & 0xff)
        };
    }

    private long calculateFileSize(Uri fileUri) {
        try (Cursor cursor = context.getContentResolver().query(fileUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
                return cursor.getLong(sizeIndex);
            }
        }
        return -1;
    }

    public Context getContext() {
        return context;
    }

    public Uri getCurrentFileUri() {
        return currentFileUri;
    }

    private String generateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss", Locale.getDefault());
        String dateTime = sdf.format(new Date());
        return "Recording_" + dateTime + ".wav";
    }
}
