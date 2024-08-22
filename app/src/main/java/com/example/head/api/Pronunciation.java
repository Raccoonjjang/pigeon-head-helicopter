package com.example.head.api;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pronunciation {



    // 여러 스크립트와 점수를 저장할 리스트 선언
    private static List<String> scripts = new ArrayList<>();
    private static List<Double> scores = new ArrayList<>();

    public static void sendAudioDataToApi(String audioPath, String script) {
        scripts.add(script); // 스크립트 저장
        new SendAudioTask().execute(audioPath);
    }

    // 비동기화 처리
    private static class SendAudioTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String audioFilePath = params[0];
            String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/PronunciationKor";
            String accessKey = "533ced8d-b2c2-4482-bc5b-da5d3aad7f24"; // 발급받은 API Key
            String languageCode = "korean"; // 언어 코드
            String audioContents = null;
            Gson gson = new Gson();
            Map<String, Object> request = new HashMap<>();
            Map<String, String> argument = new HashMap<>();

            try {
                File file = new File(audioFilePath);
                FileInputStream fis = new FileInputStream(file);
                byte[] audioBytes = new byte[(int) file.length()];
                fis.read(audioBytes);
                fis.close();

                // Android용 Base64 인코딩
                audioContents = Base64.encodeToString(audioBytes, Base64.NO_WRAP);
            } catch (IOException e) {
                e.printStackTrace();
            }

            argument.put("language_code", languageCode);
            argument.put("audio", audioContents);
            argument.put("script", scripts.get(scripts.size() - 1)); // 가장 최근 스크립트 사용
            request.put("argument", argument);

            URL url;
            HttpURLConnection con = null;
            DataOutputStream wr = null;
            InputStream is = null;
            String responseBody = null;

            try {
                url = new URL(openApiURL);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", accessKey);

                // Request body 작성
                wr = new DataOutputStream(con.getOutputStream());
                wr.write(gson.toJson(request).getBytes("UTF-8"));
                wr.flush();

                // 응답 코드 확인
                int responseCode = con.getResponseCode();
                Log.d("ApiResponse", "[responseCode] " + responseCode);

                // 응답 읽기
                is = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                responseBody = baos.toString("UTF-8");

                // 응답을 파싱하여 점수 추출
                LinkedTreeMap<String, Object> responseMap = gson.fromJson(responseBody, LinkedTreeMap.class);
                LinkedTreeMap<String, String> returnObject = (LinkedTreeMap<String, String>) responseMap.get("return_object");

                // 점수 추출 및 저장
                String scoreStr = returnObject.get("score");
                double score = Double.parseDouble(scoreStr) * 20; // 점수를 100점 만점으로 변환
                scores.add(score); // 점수를 리스트에 추가
                Log.d("ApiResponse", "Score (out of 100): " + score);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 자원 정리
                if (wr != null) {
                    try {
                        wr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (con != null) {
                    con.disconnect();
                }
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String result) {
            // 점수를 SharedPreferences에 저장하고 UI를 업데이트
            Log.d("ApiService", "API Response: " + result);
            // ResultActivity를 트리거하여 UI를 업데이트
        }
    }

    // 여러 스크립트와 점수를 가져오는 메서드
    public static List<String> getScripts() {
        return scripts;
    }

    public static List<Double> getScores() {
        return scores;
    }
}