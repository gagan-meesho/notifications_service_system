package com.assignment.notificationservice.controller.elasticsearch;

import com.assignment.notificationservice.dto.requestDTO.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.constants.elasticsearch.ElasticsearchConstants;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ElasticSearchSmsService elasticSearchSmsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ElasticSearchSmsController(ElasticSearchSmsService elasticSearchSmsService) {
        this.elasticSearchSmsService = elasticSearchSmsService;
    }

    @PostMapping("/")
    public void index(@RequestBody SmsRequestIndex smsRequestIndex) throws IOException{
        elasticSearchSmsService.index(smsRequestIndex);
    }

    @GetMapping("/{id}")
    public SmsRequestIndex getById(@PathVariable String id) {
        return elasticSearchSmsService.getById(id);
    }

    @PostMapping("/search")
    public List<SmsRequestIndex> search(@RequestBody SearchRequestDTO dto) throws IOException{
        return elasticSearchSmsService.search(dto);
    }
    @GetMapping("/")
    public List<SmsRequestIndex> getAllDocs(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "1000") int size) throws IOException {
        List<SmsRequestIndex> allDocs = new ArrayList<>();
            SearchRequest searchRequest = new SearchRequest(ElasticsearchConstants.SMS_INDEX);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from(page * size);
            sourceBuilder.size(size);
            sourceBuilder.query(QueryBuilders.matchAllQuery());

            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions
                    .DEFAULT);
            SearchHits searchHits = searchResponse.getHits();

            for (SearchHit hit : searchHits.getHits()) {
                SmsRequestIndex smsRequestIndex = objectMapper.readValue(hit.getSourceAsString(), SmsRequestIndex.class);
                allDocs.add(smsRequestIndex);
            }
        return allDocs;
    }
}
