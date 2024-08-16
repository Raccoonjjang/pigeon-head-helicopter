package com.example.head;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.head.recorder.AudioRecorderManager;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WordActivity extends AppCompatActivity {

    private TextView randomTextView;
    private List<String> data = new ArrayList<>();
    private Random random = new Random();
    private static final String TAG = "WordActivity";
    private AudioRecorderManager audioRecorderManager;
    private Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioRecorderManager = new AudioRecorderManager(this);

        if (audioRecorderManager == null) {
            Log.e(TAG, "audioRecorderManager is null after initialization");
        } else {
            Log.d(TAG, "audioRecorderManager initialized successfully");
        }
        setupUI();
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
    private void setupUI() {
        setContentView(R.layout.activity_word);  // activity_word.xml 레이아웃을 설정

        randomTextView = findViewById(R.id.randomTextView);
        recordButton = findViewById(R.id.recordButton);

        String filePath = this.getExternalFilesDir(null).getAbsolutePath() + "/myrecording.3gp";

        // 엑셀 파일에서 데이터를 읽어옵니다.
        readExcel();

        // 첫 번째 단어를 설정합니다.

        recordButton.setOnClickListener(v -> {
            if (audioRecorderManager != null && !audioRecorderManager.isRecording()) {
                audioRecorderManager.startRecording();
                recordButton.setText("Stop Recording");
                if (!data.isEmpty()) {
                    randomTextView.setText(getRandomWord());
                } else {
                    randomTextView.setText("읽어오지 못했습니다.");
                }
            } else {
                audioRecorderManager.stopRecording();
                recordButton.setText("Start Recording");
            }
        });
    }
    private String getRandomWord() {
        if (!data.isEmpty()) {
            return data.get(random.nextInt(data.size()));
        } else {
            return "No data";
        }
    }
}
