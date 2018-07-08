package com.yidianling.zj.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.yidianling.zj.android.application.MyApplication;


public class SharedPreferencesEditor {

	private static final String app_sharedPreference_name = "yidianling_zj";
	private static final String user_info = "user_info";
	public static final String first_start = "first_start";
	public static final String TEMP_TEST = "temp_test";//临时SP

	/********************************************自定义文件存储***********************************************/

	public static void putFileString(String fileName,String key,String value) {
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				fileName, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getFileString(String fileName,String key) {
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				fileName, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString(key, "");
	}

	public static void writeSharedPreference(Context context, String key, String value) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				app_sharedPreference_name, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String readSharedPreference(Context context, String key) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				app_sharedPreference_name, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString(key, "");
	}

	public static void putString(String key, String value) {
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				TEMP_TEST, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key){
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				TEMP_TEST, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString(key, "");
	}

	public static void putBoolean(String key, boolean value) {
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				TEMP_TEST, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void putInt(String key, int value) {
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				TEMP_TEST, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key) {
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				TEMP_TEST, Activity.MODE_PRIVATE);
		return mySharedPreferences.getBoolean(key, false);
	}

	public static int getInt(String key){
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				TEMP_TEST, Activity.MODE_PRIVATE);
		return mySharedPreferences.getInt(key, 0);
	}

	public static void writeUserInfoPreferences(Context context, String key, String value) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				user_info, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void writeUserInfoPreferencesLong(Context context, String key, long value) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				user_info, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static String readUserInfoPreference(Context context, String key) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				user_info, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString(key, "");
	}

	public static long readUserInfoPreferenceInt(Context context, String key) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				user_info, Activity.MODE_PRIVATE);
		return mySharedPreferences.getLong(key, 0);
	}

	public static void removeUserInfoPreference(Context context, String key) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				user_info, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	public static void saveValue(Context context , String SPKey, String key, int val){
		SharedPreferences sp = context.getSharedPreferences(SPKey, 0);
		sp.edit().putInt(key, val).commit();
	}

	public static int getIntValueFromSP(Context context, String SPKey, String key){
		SharedPreferences sp = context.getSharedPreferences(SPKey, 0);
		return sp.getInt(key, 0);
	}
	public static void clearValues(Context context, String SPKey){
		SharedPreferences sp = context.getSharedPreferences(SPKey, 0);
		sp.edit().clear().commit();
	}

	public static final void clear(){
		SharedPreferences mySharedPreferences = MyApplication.getMyApplication().getSharedPreferences(
				app_sharedPreference_name, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.clear();
		editor.commit();
	}
}
