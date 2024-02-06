package com.assignment.notificationservice.service.elasticsearch;

import com.assignment.notificationservice.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.elasticsearch.helper.Indices;
import com.assignment.notificationservice.elasticsearch.util.SearchUtil;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ElasticSearchSmsService {

    private ObjectMapper MAPPER=new ObjectMapper();
    private RestHighLevelClient client;
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchSmsService.class);

    @Autowired
    public ElasticSearchSmsService(RestHighLevelClient restHighLevelClient){
        client=restHighLevelClient;
    }

    public List<SmsRequestIndex> search(final SearchRequestDTO dto) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.SMS_INDEX,
                dto
        );

        return searchInternal(request);
    }

    public List<SmsRequestIndex> getAllSmsCreatedSince(final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.SMS_INDEX,
                "created",
                date
        );

        return searchInternal(request);
    }

    public List<SmsRequestIndex> searchCreatedSince(final SearchRequestDTO dto, final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.SMS_INDEX,
                dto,
                date
        );

        return searchInternal(request);
    }

    private List<SmsRequestIndex> searchInternal(final SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<SmsRequestIndex> vehicles = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), SmsRequestIndex.class)
                );
            }

            return vehicles;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
public Boolean index(SmsRequestIndex smsRequestIndex) {
    try {
        if (smsRequestIndex == null) {
            LOG.error("SmsRequestIndex is null.");
            return false;
        }

        final String smsRequestAsString = MAPPER.writeValueAsString(smsRequestIndex);
        if (smsRequestAsString == null) {
            LOG.error("Failed to convert SmsRequestIndex to JSON.");
            return false;
        }

        LOG.info("SMS Request as JSON: {}", smsRequestAsString);

        final IndexRequest request = new IndexRequest(Indices.SMS_INDEX);
        request.id(String.valueOf(smsRequestIndex.getId()));  // Ensure ID is converted to String
        request.source(smsRequestAsString, XContentType.JSON);

        LOG.info("Index Request: {}", request);

        final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        if (response != null) {
            LOG.info("Index Response: {}", response);
            if (response.status() != null && response.status().equals(RestStatus.CREATED)) {
                return true;
            } else {
                LOG.error("Index request failed. Response status is null or not CREATED.");
            }
        } else {
            LOG.error("Index Response is null.");
        }

    } catch (Exception e) {
        LOG.error("Error indexing SMS request.", e);
    }
    return false;
}


    public SmsRequestIndex getById(String id) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(Indices.SMS_INDEX, id),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), SmsRequestIndex.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
