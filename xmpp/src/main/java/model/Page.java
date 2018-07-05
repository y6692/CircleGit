package model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by djy
 * 2017/7/12 0012 下午 5:25
 */

public class Page<E> implements Serializable {
    private int total;
    private int pageSize;
    private List<E> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<E> getRows() {
        return rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
    }
}
