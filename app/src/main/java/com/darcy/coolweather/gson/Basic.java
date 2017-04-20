package com.darcy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by csqiang
 * Created on 2017/4/19-21:31.
 * Description:
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }

}
