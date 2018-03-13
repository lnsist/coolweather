package util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.City;
import db.County;
import db.Province;

/**
 * 解析并处理省市县数据
 * Created by Administrator on 2018-03-12.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param response 返回的数据
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                // 数据分组
                JSONArray allProvinces = new JSONArray(response);
                // 遍历
                for (int i = 0; i < allProvinces.length(); i++) {
                    // 读取单个数据
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    // 创建实体类
                    Province province = new Province();
                    // 获取各个数据
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    // 将数据存储到数据库中
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     *
     * @param response   返回的数据
     * @param provinceId 省级id
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                // 数据分组
                JSONArray allCities = new JSONArray(response);
                // 遍历
                for (int i = 0; i < allCities.length(); i++) {
                    // 读取单个数据
                    JSONObject cityObject = allCities.getJSONObject(i);
                    // 创建实体类
                    City city = new City();
                    // 获取各个数据
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    // 将数据存储到数据库中
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     *
     * @param response 返回的数据
     * @param cityId   市级id
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                // 数据分组
                JSONArray allCounties = new JSONArray(response);
                // 遍历
                for (int i = 0; i < allCounties.length(); i++) {
                    // 读取单个数据
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    // 创建实体类
                    County county = new County();
                    // 获取各个数据
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    // 将数据存储到数据库中
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
