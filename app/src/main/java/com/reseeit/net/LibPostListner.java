package com.reseeit.net;

public interface LibPostListner {
    public void onPostResponseComplete(Object clsGson, int requestCode);

    public void onPostResponseError(String errorMessage, int requestCode);
}
