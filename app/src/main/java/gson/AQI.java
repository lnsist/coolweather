package gson;

/**
 * 空气指数
 * Created by Administrator on 2018-03-13.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;

        public String pm25;
    }
}
