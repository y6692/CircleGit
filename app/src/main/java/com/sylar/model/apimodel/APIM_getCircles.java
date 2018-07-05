package com.sylar.model.apimodel;

import com.sylar.model.Page;
import com.sylar.model.Room;

import java.util.List;

/**
 * Created by yy
 * 2017/7/12 0012 下午 4:46
 */

public class APIM_getCircles extends CommonResult {
    private Page<Room> results;

    public Page<Room> getResults() {
        return results;
    }

    public void setResults(Page<Room> results) {
        this.results = results;
    }
}
