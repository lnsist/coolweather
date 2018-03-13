package com.xzh.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.xzh.coolweather.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 缓存判断
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 是否已存在天气信息
        if (prefs.getString("weather", null) != null) {
            // 存在 -- 直接跳转天气页面
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
