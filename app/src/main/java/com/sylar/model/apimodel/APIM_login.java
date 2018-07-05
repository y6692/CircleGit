package com.sylar.model.apimodel;

import com.sylar.model.Page;
import com.sylar.model.Room;
import com.sylar.model.User;

/**
 * Created by djy
 * 2017/7/12 0012 下午 4:46
 */

public class APIM_login extends CommonResult {
    private User results;

    public User getResults() {
        return results;
    }

    public void setResults(User results) {
        this.results = results;
    }
}
