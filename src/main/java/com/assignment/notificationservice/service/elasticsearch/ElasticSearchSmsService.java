package com.assignment.notificationservice.service.elasticsearch;

import com.assignment.notificationservice.constants.elasticsearch.ElasticsearchConstants;
import com.assignment.notificationservice.dto.requestDTO.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.exception.CustomIOException;
import com.assignment.notificationservice.helper.elasticsearch.SearchBuilderHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ElasticSearchSmsService {

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestHighLevelClient client;

    @Autowired
    public ElasticSearchSmsService(RestHighLevelClient restHighLevelClient) {
        client = restHighLevelClient;
    }

    public List<SmsRequestIndex> search(final SearchRequestDTO dto) throws IOException{
            final SearchRequest request = SearchBuilderHelper.buildSearchRequest(ElasticsearchConstants.SMS_INDEX, dto);
            return searchInternal(request);
    }

    private List<SmsRequestIndex> searchInternal(final SearchRequest request)  {
        if (Objects.isNull(request)) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }
        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<SmsRequestIndex> result = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                result.add(
                        mapper.readValue(hit.getSourceAsString(), SmsRequestIndex.class)
                );
            }

            return result;
        } catch (IOException ex) {
            throw new CustomIOException("IO exception occured in elastic search");
        }

    }

    public Boolean index(SmsRequestIndex smsRequestIndex) throws Exception {

            if (smsRequestIndex == null) {
                log.error("SmsRequestIndex is null.");
                return false;
            }
            final String smsRequestAsString = mapper.writeValueAsString(smsRequestIndex);
            if (smsRequestAsString == null) {
                log.error("Failed to convert SmsRequestIndex to JSON.");
                return false;
            }


            final IndexRequest request = new IndexRequest(ElasticsearchConstants.SMS_INDEX);
            request.id(String.valueOf(smsRequestIndex.getId()));
            request.source(smsRequestAsString, XContentType.JSON);

        try {
            client.index(request, RequestOptions.DEFAULT);

            return true;
        }catch (Exception e){
            log.error("exception occured while indexing.");
            return false;
        }
    }
    
    public SmsRequestIndex getById(String id) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(ElasticsearchConstants.SMS_INDEX, id),
                    RequestOptions.DEFAULT
            );
            if (Objects.isNull(documentFields) || documentFields.isSourceEmpty()) {
                return null;
            }
            SmsRequestIndex smsRequestIndex = mapper.readValue(documentFields.getSourceAsString(), SmsRequestIndex.class);
            return smsRequestIndex;
        } catch (final Exception e) {
            log.error("Error fetching SMS request with ID " + id);
            return null;
        }
    }

}
