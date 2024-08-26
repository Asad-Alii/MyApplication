package com.app.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.myapplication.models.User;
import com.google.gson.Gson;

public class PrefUtils {

    private static final String my_prefs= "My_Prefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static PrefUtils instance;

    private PrefUtils(Context context){
        sharedPreferences = context.getSharedPreferences(my_prefs, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PrefUtils getInstance(Context context){
        if(instance == null){
            instance = new PrefUtils(context);
        }
        return instance;
    }

    public boolean setUser(User user){

        Gson gson = new Gson();
        String userString = gson.toJson(user);
        editor.putString("user", userString);
        editor.apply();
        return true;
    }

    public User getUser(){
        Gson gson = new Gson();
        String userString = sharedPreferences.getString("user", "");
        return gson.fromJson(userString, User.class);
    }

    public boolean clearPrefs(){
        editor.remove("user");
        editor.apply();
        return true;
    }
}
