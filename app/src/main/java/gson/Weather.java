package gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气 -- 将各个子类组合
 * Created by Administrator on 2018-03-13.
 */

public class Weather {
    /**
     * 返回结果
     */
    public String status;
    /**
     * 基本信息
     */
    public Basic basic;
    /**
     * 空气指数
     */
    public AQI aqi;
    /**
     * 当前天气
     */
    public Now now;
    /**
     * 生活建议
     */
    public Suggestion suggestion;
    /**
     * 未来天气
     */
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
