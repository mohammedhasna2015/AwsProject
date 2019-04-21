package com.mapstest1016aws.awsprojectandroid.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Appcontroll extends Application {

    public static SharedPreferences sharedpreferences;
    public static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedpreferences = getSharedPreferences("AwsMohamedhaasna", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

    }

}
