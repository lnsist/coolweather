package com.xzh.coolweather.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xzh.coolweather.R;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import db.City;
import db.County;
import db.Province;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;

/**
 * Created by Administrator on 2018-03-12.
 */

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    /**
     * 省级
     */
    public static final int LEVEL_PROVINCE = 0;
    /**
     * 市级
     */
    public static final int LEVEL_CITY = 1;
    /**
     * 县级
     */
    public static final int LEVEL_COUNTY = 2;
    /**
     * 进度条
     */
    private ProgressDialog progressDialog;
    /**
     * 标题显示
     */
    private TextView titleText;
    /**
     * 返回按钮
     */
    private Button backButton;
    /**
     * 展示数据
     */
    private ListView listView;
    /**
     * 适配器
     */
    private ArrayAdapter<String> adapter;
    /**
     * 显示数据表
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    /**
     * 创建view
     *
     * @param inflater           布局填充器
     * @param container          容器
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // 获取自定义布局
        View view = inflater.inflate(R.layout.choose_area, container, false);
        // 获取各个控件
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        // 设置适配器属性
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        // 添加适配器
        listView.setAdapter(adapter);
        return view;
    }

    /**
     * 活动创建
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 条目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 判断当前等级
                if (currentLevel == LEVEL_PROVINCE) {
                    // 获取当前点击内容
                    selectedProvince = provinceList.get(i);
                    // 查询下级内容
                    queryCities();
                    // 判断当前等级
                } else if (currentLevel == LEVEL_CITY) {
                    // 获取当前点击内容
                    selectedCity = cityList.get(i);
                    // 查询下级内容
                    queryCounties();
                }
            }
        });
        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断当前级别
                if (currentLevel == LEVEL_COUNTY) {
                    // 查询上级内容
                    queryCities();
                    // 判断当前级别
                } else if (currentLevel == LEVEL_CITY) {
                    // 查询上级内容
                    queryProvinces();
                }
            }
        });
        // 查询省级内容
        queryProvinces();
    }

    /**
     * 查询全国所有的省, 优先从数据库查询, 如果没有查询到再通过网络查询
     */
    private void queryProvinces() {
        // 设置当前标题
        titleText.setText("中国");
        // 返回按钮隐藏
        backButton.setVisibility(View.GONE);
        // 数据库查询所有省数据
        provinceList = DataSupport.findAll(Province.class);
        // 判断数据库是否存在数据
        if (provinceList.size() > 0) {
            // 清除显示数据
            dataList.clear();
            // 遍历
            for (Province province : provinceList) {
                // 添加到显示数据列表中
                dataList.add(province.getProvinceName());
            }
            // 适配器刷新
            adapter.notifyDataSetChanged();
            // 选中列表第一个
            listView.setSelection(0);
            // 设置当前级别
            currentLevel = LEVEL_PROVINCE;
        } else {  // 数据库没有数据, 通过网络查询
            // 网络请求地址
            String address = "http://guolin.tech/api/china";
            // 通过网络查询内容
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询全国所有的市, 优先从数据库查询, 如果没有查询到再通过网络查询
     */
    private void queryCities() {
        // 设置当前标题
        titleText.setText(selectedProvince.getProvinceName());
        // 返回按钮显示
        backButton.setVisibility(View.VISIBLE);
        // 数据库根据省级id查询所有城市数据
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        // 判断数据库是否存在数据
        if (cityList.size() > 0) {
            // 清除显示数据
            dataList.clear();
            // 遍历
            for (City city : cityList) {
                // 添加到显示数据列表中
                dataList.add(city.getCityName());
            }
            // 适配器刷新
            adapter.notifyDataSetChanged();
            // 选中列表第一个
            listView.setSelection(0);
            // 设置当前级别
            currentLevel = LEVEL_CITY;
        } else {  // 数据库没有数据, 通过网络查询
            // 当前省级代码
            int provinceCode = selectedProvince.getProvinceCode();
            // 网络请求地址
            String address = "http://guolin.tech/api/china/" + provinceCode;
            // 通过网络查询内容
            LogUtil.v(TAG,"address = " + address);
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询全国所有的县, 优先从数据库查询, 如果没有查询到再通过网络查询
     */
    private void queryCounties() {
        // 设置当前标题
        titleText.setText(selectedCity.getCityName());
        // 返回按钮显示
        backButton.setVisibility(View.VISIBLE);
        // 数据库根据市级id查询所有县数据
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        // 判断数据库是否存在数据
        if (countyList.size() > 0) {
            // 清除显示数据
            dataList.clear();
            // 遍历
            for (County county : countyList) {
                // 添加到显示数据列表中
                dataList.add(county.getCountyName());
            }
            // 适配器刷新
            adapter.notifyDataSetChanged();
            // 选中列表第一个
            listView.setSelection(0);
            // 设置当前级别
            currentLevel = LEVEL_COUNTY;
        } else {  // 数据库没有数据, 通过网络查询
            // 当前省级代码
            int provinceCode = selectedProvince.getProvinceCode();
            // 当前市级代码
            int cityCode = selectedCity.getCityCode();
            // 网络请求地址
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            LogUtil.v(TAG,"address = " + address);
            // 通过网络查询内容

            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        // 显示进度对话框
        showProgressDialog();
        // 网络查询并返回数据
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 获取返回响应
                String responseText = response.body().string();
                // 结果
                boolean result = false;
                // 省级 -- 解析储存数据
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                    // 市级 -- 解析储存数据
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                    // 县级 -- 解析储存数据
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                // 查询结果
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 关闭进度对话框
                            closeProgressDialog();
                            // 省级
                            if ("province".equals(type)) {
                                // 回调
                                queryProvinces();
                                // 市级
                            } else if ("city".equals(type)) {
                                // 回调
                                queryCities();
                                // 县级
                            } else if ("county".equals(type)){
                                // 回调
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 关闭进度对话框
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**显示进度对话框*/
    private void showProgressDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**关闭进度对话框*/
    private void closeProgressDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
