package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.IOException;

import gson.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.Utility;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 更新天气
        updateWeather();
        // 更新必应每日一图
        updateBingPic();
        // 定时任务
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 8小时毫秒
        int anHout = 8 * 60 * 60 * 1000;
        // 当前系统时间 + 8小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anHout;
        // 开启更新服务
        Intent i = new Intent(this, AutoUpdateService.class);
        // 延时意图
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        // 取消上一次定时任务
        manager.cancel(pi);
        // 重新开启定时任务
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return  super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        // 获取缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 缓存天气缓存信息
        String weatherString = prefs.getString("weather", null);
        // 判断是否存在
        if (!TextUtils.isEmpty(weatherString)) {
            // 存在 -- 获取当前天气信息对象
            final Weather weather = Utility.handleWeatherResponse(weatherString);
            // 获取天气id
            final String weatherId = weather.basic.weatherId;
            // 服务地址
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=33635dbdb3f44ffb932fa05c0c9e67ca";
            // 访问服务获取信息并处理
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 获取响应信息
                    String responseText = response.body().string();
                    // 解析响应信息
                    Weather weather1 = Utility.handleWeatherResponse(responseText);
                    // 判断是否获取成功
                    if (weather != null && "okj".equals(weather.status)) {
                        // 获取修改缓存
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        // 添加缓存信息
                        editor.putString("weather", responseText);
                        // 提交缓存
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        // 必应每日一图地址
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        // 访问服务获取信息并处理
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 获取响应
                String bingPic = response.body().string();
                // 获取缓存修改
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                // 添加缓存
                editor.putString("bing_pic", bingPic);
                // 提交缓存
                editor.apply();
            }
        });
    }
}
