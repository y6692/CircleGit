package model.apimodel;


import model.Page;
import model.User;

/**
 * Created by djy
 * 2017/7/12 0012 下午 4:46
 */

public class APIM_getUsers extends CommonResult {
    private Page<User> results;

    public Page<User> getResults() {
        return results;
    }

    public void setResults(Page<User> results) {
        this.results = results;
    }
}
