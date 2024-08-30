package com.app.myapplication.utils;


import com.app.myapplication.R;
import com.bumptech.glide.request.RequestOptions;

public class CommonFunctions {

    public static RequestOptions getRequestoptions(){

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.avatar_icon);
        requestOptions.error(R.drawable.avatar_icon);
        requestOptions.skipMemoryCache(true);
        requestOptions.fitCenter();

        return requestOptions;
    }
}
