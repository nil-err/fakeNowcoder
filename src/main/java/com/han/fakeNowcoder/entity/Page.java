package com.han.fakeNowcoder.entity;

/**
 * 封装分页相关的信息
 *
 * @author imhan
 */
public class Page {

    // 当前页码
    private int current = 1;
    // 显示上限
    private int limit = 10;
    // 数据总数（计算总页数）
    private int rows;
    // 查询路径 （用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return 返回当前页current 在 limit 条件下的起始行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * @return 返回在当前 limit 下总页数
     */
    public int getTotalPagesNum() {
        if ((rows & limit) == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * @return 起始页码，即离 current 最近的 X 页
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * @return 终止页码，即离 current 最近的 X 页
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotalPagesNum();
        return to > total ? total : to;
    }
}
