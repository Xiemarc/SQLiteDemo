package com.yinshua.plugincore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * des:所有能加载插件activity的activity必须实现plugininterface接口
 * author: marc
 * date:  2017/7/12 22:20
 * email：aliali_ha@yeah.net
 */

public class BasePluginActivity extends Activity implements PluginInterface {
    protected Activity that;

    @Override
    public void attach(Activity proxyActivity) {
        that = proxyActivity;
    }


    @Override
    public void onCreate(Bundle saveInstanceState) {

    }

    @Override
    public void setContentView(View view) {
        that.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        that.setContentView(view, params);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        that.setContentView(layoutResID);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        that.addContentView(view, params);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return that.findViewById(id);
    }

    @Override
    public Intent getIntent() {
        return that.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        return that.getClassLoader();
    }

    @Override
    public Resources getResources() {
        return that.getResources();
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return that.getLayoutInflater();
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return that.getMenuInflater();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return that.getSharedPreferences(name, mode);
    }

    @Override
    public Context getApplicationContext() {
        return that.getApplicationContext();
    }

    @Override
    public WindowManager getWindowManager() {
        return that.getWindowManager();
    }

    @Override
    public Window getWindow() {
        return that.getWindow();
    }

    @Override
    public void startActivity(Intent intent) {
        that.startActivity(intent);
    }


    @Override
    public Object getSystemService(String name) {
        return that.getSystemService(name);
    }

    @Override
    public void finish() {
        that.finish();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onStart() {

    }

    @Override
    protected void onRestart() {
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


}
