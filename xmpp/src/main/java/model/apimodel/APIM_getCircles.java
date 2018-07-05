package model.apimodel;


import model.Page;
import model.Room;

/**
 * Created by djy
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
