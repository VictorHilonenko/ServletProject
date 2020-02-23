package beauty.scheduler.dao.core;

import java.util.List;

import static beauty.scheduler.util.AppConstants.DEFAULT_PAGE_SIZE;

public class Pagination<T> {
    private int page;
    private int pageSize;
    private String queryCount;
    private int totalRecords;
    private int totalPages;
    private List<T> items;

    public Pagination() {
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public Pagination(int page, int pageSize, String queryCount) {
        this.page = page;
        this.pageSize = pageSize;
        this.queryCount = queryCount;
    }

    public Pagination(int page, int pageSize, int totalRecords, List<T> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.setTotalRecords(totalRecords);
        this.items = items;
    }

    void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;

        totalPages = 0;
        if (pageSize != 0) {
            totalPages = totalRecords / pageSize + 1;
        }
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasNext() {
        return page < totalPages - 1;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    String getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(String queryCount) {
        this.queryCount = queryCount;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    void setItems(List<T> items) {
        this.items = items;
    }
}