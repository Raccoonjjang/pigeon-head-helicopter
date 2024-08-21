package com.example.head;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.head.recorder.SpeechRecognition;
import com.example.head.recorder.AudioRecorderManager;
import com.example.head.models.Word;
public class SentenceActivity extends AppCompatActivity implements SpeechRecognition.SpeechRecognitionListener {
    // <editor-fold desc="객체 선언">
    private TextView pView;
    private TextView wView;
    private List<String> data = new ArrayList<>();
    private Random random = new Random();
    private static final String TAG = "WordActivity";
    private AudioRecorderManager audioRecorderManager;
    private SpeechRecognition speechRecognition;
    private Button recordButton;
    private String scripts;
    private int resultCounter = 0; // 결과를 세는 카운터
    private List<String> wordList; // 단어 리스트를 저장할 변수
    // </editor-fold>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioRecorderManager = new AudioRecorderManager(this);
        Word word = new Word(this);
        this.wordList = word.getWords(); // 단어 리스트를 가져옴
        setupUI();
        speechRecognition.setSpeechRecognitionListener(this);
    }
    // SpeechRecognized Callback
    public void onSpeechRecognized(String result) {
        if (audioRecorderManager != null && !audioRecorderManager.isRecording()) {
            scripts = result;
            audioRecorderManager.startRecording();
            recordButton.setText("녹음 진행중");
        }
    }
    // setupUI
    private void setupUI() {
        setContentView(R.layout.activity_sentence);
        pView = findViewById(R.id.pView);
        wView = findViewById(R.id.wView);
        recordButton = findViewById(R.id.recordButton);

        speechRecognition = new SpeechRecognition(this, pView,wView, recordButton);
        String filePath = this.getExternalFilesDir(null).getAbsolutePath() + "/myrecording.3gp";

        // 첫 문장 설정
        if (wordList != null && !wordList.isEmpty()) {
            int index = resultCounter % wordList.size();
            String word = wordList.get(index);
            wView.setText(word);
        } else {
            wView.setText("문장 없음");
        }

        recordButton.setOnClickListener(v -> {
            if (audioRecorderManager != null && !audioRecorderManager.isRecording()) {
                speechRecognition.startListening();
                recordButton.setText("녹음 진행중");
            } else {
                audioRecorderManager.stopRecording(scripts);
                recordButton.setText("녹음 시작");
                resultCounter++;
                if(resultCounter == 1){
                    Intent intent = new Intent(SentenceActivity.this, ResultActivity.class);
                    startActivity(intent);
                } else {
                    // TextView에 "0/5", "1/5" 이런식으로 표시
                    String resultText = resultCounter + "/5";
                    pView.setText(resultText);
                    if (wordList != null && !wordList.isEmpty()) {
                        int index = resultCounter % wordList.size(); // 인덱스를 단어 리스트의 크기로 나눈 나머지
                        String word = wordList.get(index);
                        wView.setText(word);
                    } else {
                        wView.setText("단어 없음");
                    }
                }
            }
        });
    }
    // </editor-fold>
}
