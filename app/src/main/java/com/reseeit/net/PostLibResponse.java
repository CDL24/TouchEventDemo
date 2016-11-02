package com.reseeit.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PostLibResponse extends AsyncTask<Void, String, String> {
    private Context context;
    private LibPostListner listener;
    private ProgressDialog dialog;
    private String url;
    private boolean isProgeress = false;
    private int requestCode;
    private Object object;
    private Map<String, String> parmas;
//
//	@Override
//	protected String doInBackground(Void... args) {
//		try {
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpContext localContext = new BasicHttpContext();
//			HttpPost httpPost = new HttpPost(url);
//			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//			for (Entry<String, String> param : parmas.entrySet()) {
//				entity.addPart(param.getKey(), new StringBody(param.getValue()));
//			}
//			httpPost.setEntity(entity);
//			HttpResponse response = httpClient.execute(httpPost, localContext);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//			String sResponse = "", line = null;
//			while ((line = reader.readLine()) != null) {
//				sResponse += line;
//			}
//			return sResponse;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}


//    @Override
//    protected String doInBackground(Void... args) {
//        try {
//            URL url;
//            URLConnection urlConn;
//            DataOutputStream printout;
//            DataInputStream input;
//            url = new URL(this.url);
//            urlConn = url.openConnection();
//            urlConn.setDoInput(true);
//            urlConn.setDoOutput(true);
//            urlConn.setUseCaches(false);
////            urlConn.setRequestProperty("Content-Type", "application/json");
//            urlConn.setRequestProperty("Content-Type", "application/UTF-8");
//            urlConn.setRequestProperty("Host", url.getHost());
//            urlConn.connect();
//
//            String urlParameters = "";
//            for (Map.Entry<String, String> param : parmas.entrySet()) {
//                if (!urlParameters.equals("")) {
//                    urlParameters += "&";
//                }
//                urlParameters = (param.getKey() + "=" + param.getValue());
//            }
//            byte[] postData = urlParameters.getBytes("UTF-8");
//
//            // Send POST output.
//            printout = new DataOutputStream(urlConn.getOutputStream());
////            String encoded = URLEncoder.encode(jsonParam.toString(), "UTF-8");
////            printout.writeUTF(encoded);
//            printout.write(postData);
//            printout.flush();
//            printout.close();
//
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    urlConn.getInputStream(), "UTF-8"));
//            String line = null;
//            String sResponse = "";
//            while ((line = br.readLine()) != null) {
//                sResponse += line;
//            }
//            br.close();
//
//            return sResponse;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

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

    public PostLibResponse(LibPostListner listener, Object clsModel, Context context, Map<String, String> parmas, String getURL, int requestCode) {
        this.listener = listener;
        this.requestCode = requestCode;
        this.url = getURL;
        this.context = context;
        this.parmas = parmas;
        object = clsModel;
        execute();
    }

    public PostLibResponse(LibPostListner listener, Object clsModel, Context context, Map<String, String> parmas, String getURL, int requestCode, boolean isProgress) {
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
//        Log.d(response);
        if (isProgeress)
            if (dialog != null)
                dialog.dismiss();
        if (response == null) {
            listener.onPostResponseError("No Response", requestCode);
            return;
        }
        try {
            Object newObject = new Gson().fromJson(response, object.getClass());
            listener.onPostResponseComplete(newObject, requestCode);
        } catch (Exception e) {
            listener.onPostResponseError("Response Format Response", requestCode);
        }
    }
}