package com.darcy.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.darcy.coolweather.gson.Forecast;
import com.darcy.coolweather.gson.Weather;
import com.darcy.coolweather.util.HttpUtils;
import com.darcy.coolweather.util.Utilities;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView svWeatherLayout;
    private TextView tvTitleCity;
    private TextView tvTitleCityUpdate;
    private TextView tvDegree;
    private TextView tvWeatherInfo;
    private LinearLayout llForecastLayout;
    private TextView tvAQI;
    private TextView tvPM25;
    private TextView tvComfort;
    private TextView tvCarWash;
    private TextView tvSport;
    private ImageView ivBingPic;

    public SwipeRefreshLayout sflRefreshWeather;
    private String mWeatherId;

    DrawerLayout drawerLayout;
    private Button btNavHOme;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        // inti componnet;
        svWeatherLayout = (ScrollView) findViewById(R.id.sv_weather_layout);
        tvTitleCity = (TextView) findViewById(R.id.tv_title_city);
        tvTitleCityUpdate = (TextView) findViewById(R.id.tv_title_update_time);
        tvDegree = (TextView) findViewById(R.id.tv_degree);
        tvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);
        llForecastLayout = (LinearLayout) findViewById(R.id.ll_forecast_layout);
        tvAQI = (TextView) findViewById(R.id.tv_aqi);
        tvPM25 = (TextView) findViewById(R.id.tv_pm25);
        tvComfort = (TextView) findViewById(R.id.tv_comfort);
        tvCarWash = (TextView) findViewById(R.id.tv_car_wash);
        tvSport = (TextView) findViewById(R.id.tv_sport);
        ivBingPic = (ImageView) findViewById(R.id.iv_bing_pic);

        sflRefreshWeather = (SwipeRefreshLayout) findViewById(R.id.srl_refresh_weather);
        sflRefreshWeather.setColorSchemeColors(R.color.colorPrimary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btNavHOme = (Button) findViewById(R.id.bt_nav_home);

        btNavHOme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            // 有缓存的时候直接去解析天气数据;
            Log.d("WeatherActivity", weatherString);
            Weather weather = Utilities.handleWeatherResponse(weatherString);

            mWeatherId = weather.basic.weatherId;
            Log.d("WeatherActivity nnull", mWeatherId);
            // 然后去展示就好了;
            showWeatherInfo(weather);
        } else {
            // 无缓存时候去服务器查询天气;
            mWeatherId = getIntent().getStringExtra("weather_id");
            Log.d("WeatherActivity null", mWeatherId);
            // 这个时候先将ScrollView设置为不可见;
            svWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        sflRefreshWeather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("WeatherActivity refresh", mWeatherId + "");
                requestWeather(mWeatherId);
            }
        });

        // 获取bing pic
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(ivBingPic);
        } else {
            loadBingPic();
        }

    }

    private void loadBingPic() {
        final String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("bing_pc", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(ivBingPic);
                    }
                });
            }
        });
    }

    public void requestWeather(String weatherId) {
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=961043815d6140fe8827c2ee76386ed9";
        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        // 表示刷新事件结束;
                        sflRefreshWeather.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utilities.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        // 表示刷新事件结束;
                        sflRefreshWeather.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        // 更新控件;
        tvTitleCity.setText(cityName);
        tvTitleCityUpdate.setText(degree);
        tvWeatherInfo.setText(weatherInfo);
        tvDegree.setText(degree);
        llForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item, llForecastLayout, false);
            TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
            TextView tvMax = (TextView) view.findViewById(R.id.tv_max);
            TextView tvMin = (TextView) view.findViewById(R.id.tv_min);
            tvDate.setText(forecast.date);
            tvInfo.setText(forecast.more.info);
            tvMax.setText(forecast.temperature.max);
            tvMin.setText(forecast.temperature.min);
            llForecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            tvAQI.setText(weather.aqi.city.aqi);
            tvPM25.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;

        tvComfort.setText(comfort);
        tvCarWash.setText(carWash);
        tvSport.setText(sport);
        svWeatherLayout.setVisibility(View.VISIBLE);

    }
}
