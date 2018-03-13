package gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来天气信息 -- 由于是多组数据, 引用时采用数组方式
 * Created by Administrator on 2018-03-13.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
