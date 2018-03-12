package db;

import org.litepal.crud.DataSupport;

/**
 * 省份
 * Created by Administrator on 2018-03-12.
 */

public class Province extends DataSupport {

    public int id;
    /**名称*/
    public String provinceName;
    /**编号*/
    public int provinceCode;
}
