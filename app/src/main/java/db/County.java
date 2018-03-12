package db;

import org.litepal.crud.DataSupport;

/**
 * 县
 * Created by Administrator on 2018-03-12.
 */

public class County extends DataSupport {
    public int id;
    /**县名*/
    public String countyName;
    /**对应天气id*/
    public int weatherId;
    /**所属市id*/
    public int cityId;
}
