package com.reseeit.util;

public class WebServices {
    public static final String DEFAULT_PASSWORD = "admin";
    //    public static final String BASE_URL = "http://reseeit.c-metric.net/webservice/";
//    public static final String BASE_URL = "http://27.54.183.141:8080/reseeit/webservice/";
    public static final String BASE_URL = "http://103.254.245.142:8080/reseeit//webservice/";
    public static final String LOGIN = BASE_URL + "authentication";
    public static final String AUTO_LOGIN = BASE_URL + "AutoAuthentication";
    public static final String REGISTER = BASE_URL + "registration";
    public static final String UPDATE_PROFILE = BASE_URL + "updateuser";
    public static final String FORGET_PASSWORD = BASE_URL + "ForgetPassword";
    public static final String UPLOAD_RECEIPT = BASE_URL + "ReceiptAdd";
    public static final String RECEIPT_LIST = BASE_URL + "ReceiptList";
    public static final String COUPON_LIST = BASE_URL + "CouponFeaturedList";
    public static final String USER_COUPON_REWARD = BASE_URL + "UserCouponReward";
    public static final String COUPON_FAVORITE = BASE_URL + "Favorite_coupon";
    public static final String INTERACTION_WATCH = BASE_URL + "InteractionWatch";
    public static final String REWARD_LIST = BASE_URL + "ClientRewardList";

    public static final String URL_TERMS = "http://www.reseeit.co/terms";
    public static final String URL_PRIVACY = "http://www.reseeit.co/privacy";
    public static final String EMAIL_ID_CONTACT_US = "contact@reseeit.co";
    public static final String UPLOAD_PROFILE_IMAGE = BASE_URL +"UploadProfileApi";
    public static final String IN_APP_ADS = BASE_URL +"InAppAds";
}
