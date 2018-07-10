package com.fff.ingood.task;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IgActivityAttendTask extends HttpPostAccessTask<JsonObject, Integer, Void> {

    public IgActivityAttendTask(AsyncResponder<Integer, Void> responder) {
        super(responder);
    }

    @Override
    protected String access(JsonObject jsonObj) {
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try {
            URL url = new URL(String.valueOf(HttpProxy.HTTP_POST_API_ACTIVITY_ATTEND));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(HttpProxy.HTTP_POST_TIMEOUT*1000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.connect();

            OutputStream os = connection.getOutputStream();
            DataOutputStream writer = new DataOutputStream(os);

            writer.write(jsonObj.toString().getBytes());
            writer.flush();
            writer.close();
            os.close();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line).append("\n");

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  null;
    }
}