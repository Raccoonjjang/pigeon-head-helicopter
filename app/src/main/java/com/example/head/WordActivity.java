package com.example.head;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.head.Recode.Recoder;  // Import the Recoder class

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WordActivity extends AppCompatActivity {

    private TextView randomTextView;
    private List<String> data = new ArrayList<>();
    private Random random = new Random();
    private static final String TAG = "WordActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private Recoder recoder;  // Declare Recoder instance

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);  // activity_word.xml 레이아웃을 설정

        randomTextView = findViewById(R.id.randomTextView);
        Button changeButton = findViewById(R.id.changeButton);
        Button recordButton = findViewById(R.id.recordButton);

        // Initialize Recoder
        recoder = new Recoder(this);

        // 엑셀 파일에서 데이터를 읽어옵니다.
        readExcel();

        // 첫 번째 단어를 설정합니다.
        if (!data.isEmpty()) {
            randomTextView.setText(getRandomWord());
        } else {
            randomTextView.setText("No data");
            Log.d(TAG, "Data list is empty after reading Excel file.");
        }

        // 버튼 클릭 이벤트를 설정합니다.
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.isEmpty()) {
                    randomTextView.setText(getRandomWord());
                } else {
                    randomTextView.setText("No data");
                }
            }
        });

        // 녹음 버튼 클릭 이벤트를 설정합니다.
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recoder.isRecording()) {
                    recoder.stopRecording();
                } else {
                    recoder.startRecording();
                }
            }
        });

        // 권한 요청
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void readExcel() {
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("sentence.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if (wb != null) {
                Sheet sheet = wb.getSheet("Sheet1");

                if (sheet != null) {
                    int rowTotal = sheet.getRows();
                    Log.d(TAG, "Reading sheet: Sheet1, total rows: " + rowTotal);

                    for (int row = 1; row < rowTotal; row++) { // 첫 번째 행은 생략
                        String contentsA = sheet.getCell(0, row).getContents(); // A 열

                        if (!contentsA.isEmpty()) {
                            data.add(contentsA);
                        }
                    }
                } else {
                    Log.d(TAG, "Sheet not found: Sheet1");
                }
            } else {
                Log.d(TAG, "Workbook is null");
            }
        } catch (IOException | BiffException e) {
            Log.e(TAG, "Error reading excel file", e);
        }
    }

    private String getRandomWord() {
        if (!data.isEmpty()) {
            return data.get(random.nextInt(data.size()));
        } else {
            return "No data";
        }
    }
}
