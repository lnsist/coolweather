package db;

import org.litepal.crud.DataSupport;

/**
 * 县
 * Created by Administrator on 2018-03-12.
 */

public class County extends DataSupport {
    private int id;
    /**县名*/
    private String countyName;
    /**对应天气id*/
    private String weatherId;
    /**所属市id*/
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
