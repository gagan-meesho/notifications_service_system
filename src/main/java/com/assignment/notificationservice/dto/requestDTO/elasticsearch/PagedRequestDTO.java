package com.assignment.notificationservice.dto.requestDTO.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.*;

public class PagedRequestDTO {
    private static final int DEFAULT_SIZE = 100;

    private int page;
    private int size;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date from;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date to;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size != 0 ? size : DEFAULT_SIZE;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Date getFrom() {
        if (this.from != null) return from;
        Calendar calendar = Calendar.getInstance();

        // Set the date to "1900-01-02"
        calendar.set(Calendar.YEAR, 1900);
        calendar.set(Calendar.MONTH, Calendar.JANUARY); // Months are zero-based
        calendar.set(Calendar.DAY_OF_MONTH, 2);

        // Obtain a Date object from the Calendar instance
        return calendar.getTime();
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to == null ? Calendar.getInstance().getTime() : to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}