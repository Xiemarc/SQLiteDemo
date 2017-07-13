package com.yinshua.plugincore;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * des:插件开发标准
 * author: marc
 * date:  2017/7/12 22:11
 * email：aliali_ha@yeah.net
 */

public interface PluginInterface {
    public void attach(Activity proxyActivity);
    public void onCreate(Bundle saveInstanceState);
    public void onStart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
    public void onSaveInstanceState(Bundle outState);
    public boolean onTouchEvent(MotionEvent event);
    public void onBackPressed();
}
