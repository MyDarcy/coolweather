package com.darcy.coolweather.gson;

/**
 * Created by csqiang
 * Created on 2017/4/19-21:33.
 * Description:
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;

        public String pm25;
    }

}
