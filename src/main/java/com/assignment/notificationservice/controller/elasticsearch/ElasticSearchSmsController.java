package com.assignment.notificationservice.controller.elasticsearch;

import com.assignment.notificationservice.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.elasticsearch.helper.Indices;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.entity.imiconnect.Sms;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/elasticsearch/")
public class ElasticSearchSmsController {
    @Autowired
    private RestHighLevelClient client;
    private  ElasticSearchSmsService elasticSearchSmsService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ElasticSearchSmsController(ElasticSearchSmsService elasticSearchSmsService){
        this.elasticSearchSmsService=elasticSearchSmsService;
    }
    @PostMapping("/")
    public void index(@RequestBody SmsRequestIndex smsRequestIndex) {
        System.out.println("here is the body"+smsRequestIndex.toString());
        elasticSearchSmsService.index(smsRequestIndex);
    }

    @GetMapping("/{id}")
    public SmsRequestIndex getById(@PathVariable  String  id) {
        return elasticSearchSmsService.getById(id);
    }
    @PostMapping("/search")
    public List<SmsRequestIndex> search(@RequestBody  SearchRequestDTO dto) {
        return elasticSearchSmsService.search(dto);
    }

    @GetMapping("/search/{date}")
    public List<SmsRequestIndex> getAllVehiclesCreatedSince(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return elasticSearchSmsService.getAllSmsCreatedSince(date);
    }

    @PostMapping("/searchcreatedsince/{date}")
    public List<SmsRequestIndex> searchCreatedSince(
            @RequestBody final SearchRequestDTO dto,
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return elasticSearchSmsService.searchCreatedSince(dto, date);
    }
    @GetMapping("/")
    public List<SmsRequestIndex> getAllDocs(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "1000") int size) {
        List<SmsRequestIndex> allDocs = new ArrayList<>();

        try {
            SearchRequest searchRequest = new SearchRequest(Indices.SMS_INDEX);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from(page * size);
            sourceBuilder.size(size);
            sourceBuilder.query(QueryBuilders.matchAllQuery());

            searchRequest.source(sourceBuilder);

            // Execute search request
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions
             .DEFAULT);
            SearchHits searchHits = searchResponse.getHits();

            // Process search hits
            for (SearchHit hit : searchHits.getHits()) {
                // Parse sourceAsString to SmsRequestIndex object
                SmsRequestIndex smsRequestIndex = objectMapper.readValue(hit.getSourceAsString(), SmsRequestIndex.class);
                allDocs.add(smsRequestIndex);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return allDocs;
    }
}
