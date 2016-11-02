package com.reseeit.net;

public interface LibPostListnerNew {
    public void onPostResponseComplete(Object clsGson, String response, int requestCode);

    public void onPostResponseError(String errorMessage, String response, int requestCode);
}
