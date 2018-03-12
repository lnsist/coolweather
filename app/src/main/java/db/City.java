package db;

import org.litepal.crud.DataSupport;

/**
 * 市
 * Created by Administrator on 2018-03-12.
 */

public class City extends DataSupport {
    public int id;
    /**市名*/
    public String cityName;
    /**市编号*/
    public int cityCode;
    /**所属省份id*/
    public int provinceId;
}
