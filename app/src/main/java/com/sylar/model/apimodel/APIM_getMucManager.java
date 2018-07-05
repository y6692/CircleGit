package com.sylar.model.apimodel;

import com.sylar.model.Room;

/**
 * Created by djy
 * 2017/7/12 0012 下午 4:46
 */

public class APIM_getMucManager extends CommonResult {
    private Room results;

    public Room getResults() {
        return results;
    }

    public void setResults(Room results) {
        this.results = results;
    }
}
