package com.fff.ingood.task;

import android.app.Activity;

import com.fff.ingood.data.IgActivity;
import com.fff.ingood.tools.ParserUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class DoActivityQueryTask  extends HttpPostAbstractTask<String, List<IgActivity>>{
    public DoActivityQueryTask(Activity activity, AsyncResponder<List<IgActivity>> responder) {
        super(activity,responder);
    }
    public DoActivityQueryTask(AsyncResponder<List<IgActivity>> responder) {
        super(responder);
    }
    @Override
    protected String access(Activity activity, String info) {
        {
            boolean result = false;
            URL url;
            BufferedReader reader = null;
            StringBuilder stringBuilder;
            String jsonString;
            HashMap<String,String> list = new HashMap<>();
            list.put("ids", info);
            jsonString = new Gson().toJson(list);


            try
            {
                // create the HttpURLConnection
                url = new URL(String.valueOf(HttpProxy.HTTP_POST_API_ACTIVITY_QUERY));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // 使用甚麼方法做連線
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(HttpProxy.HTTP_POST_TIMEOUT*1000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);                                                        //允許輸入流，即允許下載
                connection.setDoOutput(true);                                                       //允許輸出流，即允許上傳
                connection.setUseCaches(false);                                                     //設置是否使用緩存
                //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //设置请求体的类型是文本类型
                //connection.setRequestProperty("Content-Length", String.valueOf(jsonString.length()));                              //設定內容長度

                // 是否添加參數(ex : json...等)
                //connection.setDoOutput(true);

                // 設定TimeOut時間
                connection.connect();

                // 伺服器回來的參數

                // output body
                OutputStream os = connection.getOutputStream();
                DataOutputStream writer = new DataOutputStream(os);
                writer.write(jsonString.getBytes());
                //writer.writeBytes(jsonString);
                writer.flush();
                writer.close();
                os.close();

                //response body
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line + "\n");
                }
                return stringBuilder.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                // close the reader; this can throw an exception too, so
                // wrap it in another try/catch block.
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return  null;
        }
    }

    @Override
    protected List<IgActivity> parseFromResponse(String response) {
        return ParserUtils.getActivitiyList(response);
    }
}
