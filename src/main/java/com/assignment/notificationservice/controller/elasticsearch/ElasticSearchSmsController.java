package com.assignment.notificationservice.controller.elasticsearch;

import com.assignment.notificationservice.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/elasticsearch/")
public class ElasticSearchSmsController {
    private  ElasticSearchSmsService elasticSearchSmsService;
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
    public List<SmsRequestIndex> search(@RequestBody final SearchRequestDTO dto) {
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
}
