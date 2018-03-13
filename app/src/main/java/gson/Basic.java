package gson;

import com.google.gson.annotations.SerializedName;

/**
 * 基础信息
 * Created by Administrator on 2018-03-13.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}

