package com.example.head.api;import android.os.AsyncTask;
import android.util.Log;
import android.util.Base64;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiService {

    public static void sendAudioDataToApi(String audioPath) {
        new SendAudioTask().execute(audioPath);
    }

    private static class SendAudioTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String audioFilePath = params[0];
            String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition";
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

                // Write request body
                wr = new DataOutputStream(con.getOutputStream());
                wr.write(gson.toJson(request).getBytes("UTF-8"));
                wr.flush();

                // Check response code
                int responseCode = con.getResponseCode();
                Log.d("ApiResponse", "[responseCode] " + responseCode);

                // Read response
                is = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                responseBody = baos.toString("UTF-8");

                Log.d("ApiResponse", "[responseBody] " + responseBody);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Clean up resources
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
            // UI 업데이트 작업 또는 결과 처리
            Log.d("ApiService", "API Response: " + result);
        }
    }
}

