package com.xlzn.hcpda.uhf;

import android.content.Context;
import android.content.SharedPreferences;

public class HcPreferences {
    private static HcPreferences preferences;
    private ChangeSharedPreferencesPathCallBack changeSharedPreferencesPathCallBack;
    private SharedPreferences sp;

    public interface ChangeSharedPreferencesPathCallBack {
        SharedPreferences onPathChange(String str);
    }

    private HcPreferences() {
    }

    public static HcPreferences getInstance() {
        if (preferences == null) {
            synchronized (HcPreferences.class) {
                if (preferences == null) {
                    preferences = new HcPreferences();
                }
            }
        }
        return preferences;
    }

    public String getString(Context context, String path, String preferencesName) {
        return getString(context, path, preferencesName, "");
    }

    public boolean getBoolean(Context context, String path, String preferencesName) {
        return getBoolean(context, path, preferencesName, false);
    }

    public int getInt(Context context, String path, String preferencesName) {
        return getInt(context, path, preferencesName, 30);
    }

    public String getString(Context context, String path, String preferencesName, String defaultValue) {
        initSharedPreferences(context, path);
        return this.sp.getString(preferencesName, defaultValue);
    }

    public boolean getBoolean(Context context, String path, String preferencesName, boolean defaultValue) {
        initSharedPreferences(context, path);
        return this.sp.getBoolean(preferencesName, defaultValue);
    }

    public int getInt(Context context, String path, String preferencesName, int defaultValue) {
        initSharedPreferences(context, path);
        return this.sp.getInt(preferencesName, defaultValue);
    }

    public void set(Context context, String path, String preferencesName, String value) {
        initSharedPreferences(context, path);
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString(preferencesName, value);
        editor.apply();
    }

    public void set(Context context, String path, String preferencesName, boolean value) {
        initSharedPreferences(context, path);
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putBoolean(preferencesName, value);
        editor.apply();
    }

    public void set(Context context, String path, String preferencesName, int value) {
        initSharedPreferences(context, path);
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putInt(preferencesName, value);
        editor.apply();
    }

    public void commit(Context context, String path, String preferencesName, String value) {
        initSharedPreferences(context, path);
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString(preferencesName, value);
        editor.commit();
    }

    public void commit(Context context, String path, String preferencesName, boolean value) {
        initSharedPreferences(context, path);
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putBoolean(preferencesName, value);
        editor.commit();
    }

    public void commit(Context context, String path, String preferencesName, int value) {
        initSharedPreferences(context, path);
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putInt(preferencesName, value);
        editor.commit();
    }

    public void cleanAll(Context context, String path) {
        initSharedPreferences(context, path);
        SharedPreferences sharedPreferences = this.sp;
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }

    private void initSharedPreferences(Context context, String path) {
        if (this.sp == null) {
            this.sp = context.getApplicationContext().getSharedPreferences(path, 0);
            return;
        }
        ChangeSharedPreferencesPathCallBack changeSharedPreferencesPathCallBack2 = this.changeSharedPreferencesPathCallBack;
        if (changeSharedPreferencesPathCallBack2 != null) {
            this.sp = changeSharedPreferencesPathCallBack2.onPathChange(path);
        }
    }

    public SharedPreferences getSharedPreferences() {
        return this.sp;
    }

    @Deprecated
    public HcPreferences initSharedPreferences(SharedPreferences sp2) {
        this.sp = sp2;
        return this;
    }

    public HcPreferences initSharedPreferences(SharedPreferences sp2, ChangeSharedPreferencesPathCallBack changeSharedPreferencesPathCallBack2) {
        this.sp = sp2;
        this.changeSharedPreferencesPathCallBack = changeSharedPreferencesPathCallBack2;
        return this;
    }

    public ChangeSharedPreferencesPathCallBack getChangeSharedPreferencesPathCallBack() {
        return this.changeSharedPreferencesPathCallBack;
    }

    public HcPreferences setChangeSharedPreferencesPathCallBack(ChangeSharedPreferencesPathCallBack changeSharedPreferencesPathCallBack2) {
        this.changeSharedPreferencesPathCallBack = changeSharedPreferencesPathCallBack2;
        return this;
    }
}
