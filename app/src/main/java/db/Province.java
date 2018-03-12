package db;

import org.litepal.crud.DataSupport;

/**
 * 省份
 * Created by Administrator on 2018-03-12.
 */

public class Province extends DataSupport {

    private int id;
    /**名称*/
    private String provinceName;
    /**代码*/
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
