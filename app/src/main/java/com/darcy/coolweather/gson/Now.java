package com.darcy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by csqiang
 * Created on 2017/4/19-21:34.
 * Description:
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;
    }

}
