package com.cookandroid.trashapptest;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Json{
    private String str;
    private static final String TAG = "MyActivity";

    public Json() {
        str = "{";
    }
    public void putJson(String key, String value) {
        str = str + "\"" + key + "\":" + "\"" + value + "\",";
    }
    public void delJson() {
        str = "{";
    }
    public String getJson() {
        return str.substring(0, str.length()-1) + "}";
    }

    public String sendREST(String pageURL, String method, String jsonValue){

        String inputLine = null;
        StringBuffer outResult = new StringBuffer();

        URL url;
        try {
            url = new URL(pageURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.setUseCaches(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            OutputStream os = conn.getOutputStream();
            os.write(jsonValue.getBytes("UTF-8"));
            os.flush();

            int statusCode = conn.getResponseCode();
            if(statusCode != 200) return Integer.toString(statusCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));


            while((inputLine = in.readLine())!=null) {
                outResult.append(inputLine);
            }
            conn.disconnect();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return outResult.toString();
    }
    public String[] orderDict(String buf) {

        int stack = 0, j = 0;
        boolean isString = false;
        String recent;
        String arr[] = new String[100];

        for(int i = 0; i < buf.length(); i++) {
            if(buf.charAt(i) == '(') stack++;
            else if(buf.charAt(i) == ')') stack--;

            if(stack==2) {
                isString = true;
                while(buf.charAt(i) != ',') i++;
                i+=2;
                recent = "";
                while(buf.charAt(i) != ')') {
                    recent = recent + Character.toString(buf.charAt(i));
                    i++;
                }
                arr[j++] = recent;
                stack--;
            }

        }
        String ans[] = new String[j];
        for(int i=0; i<j; i++) ans[i] = arr[i];
        return ans;

    }
}