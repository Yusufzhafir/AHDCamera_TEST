package com.quectel.multicamera.utils;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zyz on 2021/3/1.
 */
public class SpUtil {

    public static final String LANGUAGE = "language";
    private static final String SP_NAME = "poemTripSpref";
    private static SpUtil spUtil;
    private static SharedPreferences hmSpref;
    private static SharedPreferences.Editor editor;
    private static boolean change = false;

    private SpUtil(Context context) {
        synchronized (GUtilMain.class) {
            hmSpref = GUtilMain.mSharedPreferences;//context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            if (hmSpref== null) {
                hmSpref = context.getSharedPreferences("multi_camera_data", Context.MODE_PRIVATE);
                editor = hmSpref.edit();
            }else {
                editor = GUtilMain.mEditor;
            }
        }
    }

    public void setChange(boolean change0){
        change = change0;
    }

    public boolean getChange(){
        return change;
    }

    public static SpUtil getInstance(Context context) {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil(context);
                }
            }
        }
        return spUtil;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return hmSpref.getString(key, LanguageType.ENGLISH.getLanguage());
    }

}
