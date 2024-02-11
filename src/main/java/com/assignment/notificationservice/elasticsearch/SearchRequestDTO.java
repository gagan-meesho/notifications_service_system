package com.assignment.notificationservice.elasticsearch;


import lombok.ToString;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchRequestDTO extends PagedRequestDTO {
    private List<String> fields;
    private String searchTerm;
    private String sortBy;
    private SortOrder order;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public String toString(){
        return PagedRequestDTO.class.toString()+" "+fields+" "+searchTerm+" "+sortBy+" "+order;
    }
}