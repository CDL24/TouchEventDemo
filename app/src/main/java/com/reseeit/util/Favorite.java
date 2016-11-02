package com.reseeit.util;

import com.google.gson.Gson;
import com.reseeit.models.Coupon;
import com.reseeit.models.Interaction;

import org.json.JSONObject;

import java.util.List;

public class Favorite {
    public String image_path;
    public Coupon data;
    public List<Interaction> interactions;

    public Favorite toFavorite(String favoriteJson) {
        return new Gson().fromJson(favoriteJson, Favorite.class);
    }

    @Override
    public String toString() {
        super.toString();
        return new Gson().toJson(this, Favorite.class);
    }
}