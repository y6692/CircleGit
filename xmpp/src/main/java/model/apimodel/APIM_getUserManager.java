package model.apimodel;


import model.User;

/**
 * @author
 * @version 1.0
 * @date 2017/7/13
 */

public class APIM_getUserManager extends CommonResult{
    private User results;

    public User getResults() {
        return results;
    }

    public void setResults(User results) {
        this.results = results;
    }
}
