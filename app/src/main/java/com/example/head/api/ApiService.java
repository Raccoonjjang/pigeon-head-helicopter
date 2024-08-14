package com.example.head.api;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiService {

    public static void sendAudioDataToApi(final String base64Audio) {
        new SendAudioTask().execute(base64Audio);
    }

    private static class SendAudioTask extends AsyncTask<String, Void, Void> {
        private static final String TAG = "ApiService";
        private static final String OPEN_API_URL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition"; // Use HTTPS
        private static final String ACCESS_KEY = "533ced8d-b2c2-4482-bc5b-da5d3aad7f24"; // Your API Key
        private static final String LANGUAGE_CODE = "korean";

        @Override
        protected Void doInBackground(String... params) {
            String base64Audio = params[0];
            Gson gson = new Gson();
            Map<String, Object> request = new HashMap<>();
            Map<String, String> argument = new HashMap<>();

            argument.put("language_code", LANGUAGE_CODE);
            argument.put("audio", base64Audio);
            request.put("argument", argument);

            HttpURLConnection con = null;
            try {
                URL url = new URL(OPEN_API_URL);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", ACCESS_KEY);

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(gson.toJson(request).getBytes("UTF-8"));
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                InputStream is = con.getInputStream();
                byte[] buffer = new byte[is.available()];
                int byteRead = is.read(buffer);
                String responseBody = new String(buffer);

                Log.i(TAG, "[responseCode] " + responseCode);
                Log.i(TAG, "[responseBody]");
                Log.i(TAG, responseBody);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return null;
        }
    }
}
