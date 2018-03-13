package com.xzh.coolweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xzh.coolweather.R;

import java.io.IOException;

import gson.Forecast;
import gson.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.Utility;

public class WeatherActivity extends AppCompatActivity {
    /**
     * 滚动显示
     */
    private ScrollView weatherLayout;
    /**
     * 城市名称
     */
    private TextView titleCity;
    /**
     * 天气更新时间
     */
    private TextView titleUpdateTime;
    /**
     * 当前温度
     */
    private TextView degreeText;
    /**
     * 天气概况
     */
    private TextView weatherInfoText;
    /**
     * 未来天气
     */
    private LinearLayout forecastLayout;
    /**
     * 空气指数
     */
    private TextView aqiText;
    /**
     * pm25
     */
    private TextView pm25Text;
    /**
     * 舒适度
     */
    private TextView comfortText;
    /**
     * 洗车指数
     */
    private TextView carWashText;
    /**
     * 运动建议
     */
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 初始化控件
        initUI();
        // 缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 获取天气缓存
        String weatherString = prefs.getString("weather", null);
        // 判断缓存是否存在
        if (!TextUtils.isEmpty(weatherString)) {
            // 存在 -- 直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            // 解析并显示天气数据
            showWeatherInfo(weather);
        } else {
            // 不存在 -- 去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            // 设置天气列表隐藏
            weatherLayout.setVisibility(View.INVISIBLE);
            // 请求网络数据
            requestWeather(weatherId);
        }
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    private void requestWeather(String weatherId) {
        // 访问天气Url
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=33635dbdb3f44ffb932fa05c0c9e67ca";
        // 网络请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败
                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 获取响应数据
                final String responseText = response.body().string();
                // 解析并获取天气实体类
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 数据不为空, 并且访问结果ok
                        if (weather != null && "ok".equals(weather.status)) {
                            // 获取缓存修改
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            // 添加数据
                            editor.putString("weather", responseText);
                            // 提交缓存信息
                            editor.apply();
                            // 解析并展示天气信息
                            showWeatherInfo(weather);
                        } else {
                            // 获取失败
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    /**
     * 处理并展示Weatehr实体类的数据
     *
     * @param weather 天气数据
     */
    private void showWeatherInfo(Weather weather) {
        // 城市名称
        String cityName = weather.basic.cityName;
        // 天气更新时间
        String updataTime = weather.basic.update.updateTime.split(" ")[1];
        // 当前温度
        String degree = weather.now.temperature + "℃";
        // 天气概况
        String weatherInfo = weather.now.more.info;

        // 设置城市名称
        titleCity.setText(cityName);
        // 设置天气更新时间
        titleUpdateTime.setText(updataTime);
        // 设置温度
        degreeText.setText(degree);
        // 设置天气概况
        weatherInfoText.setText(weatherInfo);

        // 移除所有天气信息
        forecastLayout.removeAllViews();
        // 遍历
        for (Forecast forecast : weather.forecastList) {
            // 创建布局view, 绑定forecastLayout
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            // 获取各个控件
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            // 设置预测天气时间
            dateText.setText(forecast.date);
            // 设置天气概况
            infoText.setText(forecast.more.info);
            // 设置最高温度
            maxText.setText(forecast.temperature.max + "℃");
            // 设置最低温度
            minText.setText(forecast.temperature.min + "℃");

            // 添加布局
            forecastLayout.addView(view);
        }
        // 空气指数不为空
        if (weather.aqi != null) {
            // 设置空气质量
            aqiText.setText(weather.aqi.city.aqi);
            // 设置pm25
            pm25Text.setText(weather.aqi.city.pm25);
        }
        // 获取各个生活建议
        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议: " + weather.suggestion.sport.info;

        // 设置舒适度
        comfortText.setText(comfort);
        // 设置洗车指数
        carWashText.setText(carWash);
        // 设置运动建议
        sportText.setText(sport);

        // 设置天气列表显示
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
