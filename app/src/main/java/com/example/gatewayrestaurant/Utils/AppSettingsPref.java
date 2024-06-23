package com.example.gatewayrestaurant.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettingsPref {

    private static SharedPreferences sharedPreferences;

    public static String SHARD_PREF_NAME = "Mega";
    public static String LOCATION = "location";
    public static String CART_COUNT = "cartCount";
    public static String HAS_ON_BOARDED = "onBoarding";

    private final SharedPreferences.Editor editor;

    private final Context context;

    public AppSettingsPref(Context context) {
        this.context = context;
        int PRIVATE_MODE = 0;
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
    }


    public Integer getCartCount() {
        Integer cartCount = sharedPreferences.getInt(CART_COUNT, 0);
        return cartCount;
    }

    public void setCartCount(Integer cartCount) {
        editor.putInt(CART_COUNT, cartCount);
        editor.commit();
    }
    public static void saveInt(Context context, String key, int value){
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveString(Context context,String key,String value){
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveBoolean(Context context,String key,boolean value){
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getIntValue(Context context, String key, int defaultValue){
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,defaultValue);
    }

    public static String getStringValue(Context context, String key, String defaultValue){
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME,
                Context.MODE_PRIVATE);

        return sharedPreferences.getString(key,defaultValue);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue){
        sharedPreferences = context.getSharedPreferences(SHARD_PREF_NAME,
                Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(key,defaultValue);
    }
}
