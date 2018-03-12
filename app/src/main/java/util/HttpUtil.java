package util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 网络访问工具
 * Created by Administrator on 2018-03-12.
 */

public class HttpUtil {

    /**
     * 网络请求并处理返回响应
     *
     * @param address  请求地址
     * @param callback 处理返回响应回调
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        // OKHttp客户端
        OkHttpClient client = new OkHttpClient();
        // 访问并返回响应
        Request request = new Request.Builder().url(address).build();
        // 处理返回响应
        client.newCall(request).enqueue(callback);
    }
}
