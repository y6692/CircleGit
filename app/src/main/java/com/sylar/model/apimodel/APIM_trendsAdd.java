package com.sylar.model.apimodel;

import com.sylar.model.Page;
import com.sylar.model.Room;
import com.sylar.model.Trends;

/**
 * Created by djy
 * 2017/7/12 0012 下午 4:46
 */

public class APIM_trendsAdd extends CommonResult {
    private Trends results;

    public Trends getResults() {
        return results;
    }

    public void setResults(Trends results) {
        this.results = results;
    }
}
