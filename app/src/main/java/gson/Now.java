package gson;

import com.google.gson.annotations.SerializedName;

/**
 * 当前天气
 * Created by Administrator on 2018-03-13.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
