package com.example.head.recorder;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import com.example.head.models.Word;

public class SpeechRecognition {
    // <editor-fold desc="객체생성">
    private SpeechRecognizer mRecognizer;
    private TextView pView;
    private TextView wView;
    private Button button;
    private Context context;
    private AudioRecorderManager audioRecorderManager;
    private Intent intent;

    //</editor-fold>
    public SpeechRecognition(Context context, TextView pView, TextView wView, Button button) {
        this.context = context;
        this.pView = pView;
        this.wView = wView;
        this.button = button;
        audioRecorderManager = new AudioRecorderManager(context);
        initializeSpeechRecognizer();
    }
    // <editor-fold desc="WordActivity Callback 부분">
    public interface SpeechRecognitionListener {
        void onSpeechRecognized(String result);
    }

    private SpeechRecognitionListener listener;

    public void setSpeechRecognitionListener(SpeechRecognitionListener listener) {
        this.listener = listener;
    }
    // </editor-fold>
    private void initializeSpeechRecognizer() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(context, "음성인식 시작", Toast.LENGTH_SHORT).show();
            }

            // <editor-fold desc="각종 에러 toast 부분">
            @Override
            public void onError(int error) {
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";

                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";

                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER 가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(context, "에러 발생 : " + message, Toast.LENGTH_SHORT).show();
            }
            // </editor-fold>
            // 결과값 반환
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    if (listener != null) {
                        listener.onSpeechRecognized(matches.get(0));
                        Toast.makeText(context, "한 : " + matches.get(0), Toast.LENGTH_SHORT).show();
                    }
                    button.setText("녹음 중지");
                    //textView.setText(matches.get(0));
                }
            }
            //</editor-fold>
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onPartialResults(Bundle partialResults) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    public void startListening() {
        mRecognizer.startListening(intent);
    }

}
