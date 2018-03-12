package db;

import org.litepal.crud.DataSupport;

/**
 * 市
 * Created by Administrator on 2018-03-12.
 */

public class City extends DataSupport {
    private int id;
    /**市名*/
    private String cityName;
    /**市代码*/
    private int cityCode;
    /**所属省份id*/
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
