package com.example.head;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.head.api.Pronunciation;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView[] recognizedTextViews;
    private TextView[] scoreTextViews;
    private TextView averageScoreTextView;
    private ImageView backButton;

    private double averageScore=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // XML에서 View 연결
        recognizedTextViews = new TextView[] {
                findViewById(R.id.recognized_text_view1),
                findViewById(R.id.recognized_text_view2),
                findViewById(R.id.recognized_text_view3),
                findViewById(R.id.recognized_text_view4),
                findViewById(R.id.recognized_text_view5)
        };

        scoreTextViews = new TextView[] {
                findViewById(R.id.score_text_view1),
                findViewById(R.id.score_text_view2),
                findViewById(R.id.score_text_view3),
                findViewById(R.id.score_text_view4),
                findViewById(R.id.score_text_view5)
        };

        averageScoreTextView = findViewById(R.id.average_score_text_view);
        backButton = findViewById(R.id.iv_uploaded_image_back);

        // Pronunciation 클래스에서 여러 스크립트와 점수 가져오기
        List<String> scripts = Pronunciation.getScripts();
        List<Double> scores = Pronunciation.getScores();

        // TextView에 결과 표시
        for (int i = 0; i < recognizedTextViews.length; i++) {
            if (i < scripts.size() && i < scores.size()) {
                recognizedTextViews[i].setText("대본: " + scripts.get(i));
                scoreTextViews[i].setText("점수: " + String.format("%.2f", scores.get(i)));
            }
        }

        // 평균 점수 계산 및 표시
        for (int i = 0; i < scores.size(); i++) {
            averageScore = scores.get(i)+averageScore;
        }
        averageScore = averageScore/scores.size();
        averageScoreTextView.setText("평균: " + String.format("%.2f", averageScore));


        // 평균 점수를 SharedPreferences에 저장
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("saved_average_score", (float) averageScore);
        editor.apply();

        // 뒤로 가기 버튼에 클릭 리스너 설정
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료 (뒤로 가기)
            }
        });
    }
}