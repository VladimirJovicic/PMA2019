package com.example.donesiklon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class SaveSharedPreference
{
    static final String PREF_USER_NAME= "username";



    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void clearUserName(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_NAME);
        //editor.clear(); //clear all stored data
        Log.i("OBRISAO",  "trebalo bi");
        editor.commit();
    }

    public static void setFetched(Context ctx, String fetched)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("fetched", fetched);
        editor.commit();
    }

    public static String getFetched(Context ctx)
    {
        return getSharedPreferences(ctx).getString("fetched", "");
    }


}