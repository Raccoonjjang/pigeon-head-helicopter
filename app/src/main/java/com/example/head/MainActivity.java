package com.example.head;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Edge-to-Edge를 위한 코드
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // "단어" 버튼 클릭 시 SentenceActivity로 이동
        Button wordButton = findViewById(R.id.btn_sentence);
        wordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SentenceActivity.class);
            startActivity(intent);
        });

        // "기록" 버튼 클릭 시 ResultActivity로 이동
        Button recordButton = findViewById(R.id.btn_girok);
        recordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class); // GirokActivity에서 ResultActivity로 변경
            startActivity(intent);
        });
    }
}
