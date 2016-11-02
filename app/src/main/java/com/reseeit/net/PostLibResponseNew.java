package com.reseeit.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.reseeit.util.Utility.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PostLibResponseNew extends AsyncTask<Void, String, String> {
    private Context context;
    private LibPostListnerNew listener;
    private ProgressDialog dialog;
    private String url;
    private boolean isProgeress = false;
    private int requestCode;
    private Object object;
    private Map<String, String> parmas;

    @Override
    protected String doInBackground(Void... args) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String sResponse = "";
            try {
                urlConnection.setRequestProperty("User-Agent", "Mobile");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                String urlParameters = "";
                for (Map.Entry<String, String> param : parmas.entrySet()) {
                    if (!urlParameters.equals("")) {
                        urlParameters += "&";
                    }
                    urlParameters += (param.getKey() + "=" + param.getValue());
                }

                OutputStream os = urlConnection.getOutputStream();
                os.write(urlParameters.getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { //success
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        sResponse += line;
                    }
                    in.close();
                } else {
                    sResponse = "";
                }

            } finally {
                urlConnection.disconnect();
            }
            return sResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void onDestroy() {
        if (isProgeress)
            if (dialog != null)
                dialog.dismiss();
        this.cancel(true);
    }

    public PostLibResponseNew(LibPostListnerNew listener, Object clsModel, Context context, Map<String, String> parmas, String getURL, int requestCode) {
        this.listener = listener;
        this.requestCode = requestCode;
        this.url = getURL;
        this.context = context;
        this.parmas = parmas;
        object = clsModel;
        execute();
    }

    public PostLibResponseNew(LibPostListnerNew listener, Object clsModel, Context context, Map<String, String> parmas, String getURL, int requestCode, boolean isProgress) {
        this.listener = listener;
        this.requestCode = requestCode;
        this.url = getURL;
        this.context = context;
        this.parmas = parmas;
        object = clsModel;

        this.isProgeress = isProgress;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait");

        try {
            if (isProgeress)
                dialog.show();
        } catch (Exception e) {
        }
        try {
            execute();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.d(response);
        if (isProgeress)
            if (dialog != null)
                dialog.dismiss();
        if (response == null) {
            listener.onPostResponseError("No Response", response, requestCode);
            return;
        }
        try {
            Object newObject = new Gson().fromJson(response, object.getClass());
            listener.onPostResponseComplete(newObject, response, requestCode);
        } catch (Exception e) {
            listener.onPostResponseError("Response Format Response", response, requestCode);
        }
    }
}