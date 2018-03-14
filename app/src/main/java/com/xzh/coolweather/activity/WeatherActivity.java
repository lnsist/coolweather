package com.xzh.coolweather.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xzh.coolweather.R;

import java.io.IOException;

import gson.Forecast;
import gson.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;

public class WeatherActivity extends AppCompatActivity {

    /**
     * 必应每日一图
     */
    private ImageView bingPicImg;
    /**
     * 滚动显示
     */
    private ScrollView weatherLayout;
    /**
     * 下拉刷新
     */
    public SwipeRefreshLayout swipeRefresh;
    /**
     * 当前天气id
     */
    private String mWeatherId;
    /**
     * 选择城市
     */
    private Button navButton;
    /**
     * 滑动菜单
     */
    public DrawerLayout drawerLayout;
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
        // 判断当前版本5.0以上
        if (Build.VERSION.SDK_INT >= 21) {
            // 获取当前活动的DecorView
            View decorView = getWindow().getDecorView();
            // 改变系统UI的显示, 活动的布局显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 设置状态栏为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        // 初始化控件
        initUI();
        // 缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 获取必应每日一图缓存
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            // 存在 -- 加载图片并添加到控件中
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            // 不存在 -- 请求服务器获取
            loadBingPic();
        }
        // 获取天气缓存
        String weatherString = prefs.getString("weather", null);
        // 判断缓存是否存在
        if (!TextUtils.isEmpty(weatherString)) {
            // 存在 -- 直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            // 获取当前天气id
            mWeatherId = weather.basic.weatherId;
            // 解析并显示天气数据
            showWeatherInfo(weather);
        } else {
            // 不存在 -- 去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            // 设置天气列表隐藏
            weatherLayout.setVisibility(View.INVISIBLE);
            // 请求网络数据
            requestWeather(mWeatherId);
        }
        // 下拉监听
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 手动刷新天气信息
                requestWeather(mWeatherId);
            }
        });
    }


    /**
     * 初始化控件
     */
    private void initUI() {
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        // 设置下拉刷新进度条颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
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
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        // 城市选择
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        // 服务器地址
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        // 请求并处理响应
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 获取响应
                final String bingPic = response.body().string();
                LogUtil.v("WeatherActivity", bingPic);
                // 缓存修改
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                // 添加必应每日一图
                editor.putString("bing_pic", bingPic);
                // 提交
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 加载图片并添加到控件中
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        // 访问天气Url
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=33635dbdb3f44ffb932fa05c0c9e67ca";
        // 网络请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败
                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                // 隐藏进度条
                swipeRefresh.setRefreshing(false);
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
                            // 获取当前天气id
                            mWeatherId = weather.basic.weatherId;
                            // 解析并展示天气信息
                            showWeatherInfo(weather);
                        } else {
                            // 获取失败
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        // 隐藏进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        // 刷新必应每日一图
        loadBingPic();
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
