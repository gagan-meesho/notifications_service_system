package com.assignment.notificationservice.service.elasticsearch;

import com.assignment.notificationservice.dto.requestDTO.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.constants.elasticsearch.Indices;
import com.assignment.notificationservice.helper.elasticsearch.SearchBuilderHelper;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
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

    private final ObjectMapper MAPPER = new ObjectMapper();
    private final RestHighLevelClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSmsService.class);
    private static final Logger LOG = LOGGER;

    @Autowired
    public ElasticSearchSmsService(RestHighLevelClient restHighLevelClient) {
        client = restHighLevelClient;
    }

    public List<SmsRequestIndex> search(final SearchRequestDTO dto) {
        try {
            LOGGER.info("Building search request for DTO: {}", dto); // Log before building search request
            final SearchRequest request = SearchBuilderHelper.buildSearchRequest(Indices.SMS_INDEX, dto);
            LOGGER.info("Search request built successfully: {}", request.toString()); // Log after building search request

            return searchInternal(request);
        } catch (Exception e) {
            LOGGER.error("Error occurred during search operation", e); // Log exception
            throw new RuntimeException("Error occurred during search operation");
        }
    }

    public List<SmsRequestIndex> getAllSmsCreatedSince(final Date date) {
        try {
            LOGGER.info("Building search request for SMS created since: {}", date); // Log before building search request
            final SearchRequest request = SearchBuilderHelper.buildSearchRequest(Indices.SMS_INDEX, "createdAt", date);
            LOGGER.info("Search request built successfully: {}", request.toString()); // Log after building search request

            return searchInternal(request);
        } catch (Exception e) {
            LOGGER.error("Error occurred during search operation", e); // Log exception
            throw new RuntimeException("Error occurred during search operation");
        }
    }

    public List<SmsRequestIndex> searchCreatedSince(final SearchRequestDTO dto, final Date date) {
        try {
            LOGGER.info("Building search request for SMS created since {} with DTO: {}", date, dto); // Log before building search request
            final SearchRequest request = SearchBuilderHelper.buildSearchRequest(Indices.SMS_INDEX, dto, date);
            LOGGER.info("Search request built successfully: {}", request.toString()); // Log after building search request

            return searchInternal(request);
        } catch (Exception e) {
            LOGGER.error("Error occurred during search operation", e); // Log exception
            throw new RuntimeException("Error occurred during search operation");
        }
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
            request.id(String.valueOf(smsRequestIndex.getId()));
            request.source(smsRequestAsString, XContentType.JSON);

            LOG.info("SMS Request : {}", request);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            if (!(response.getResult() == DocWriteResponse.Result.CREATED ||
                    response.getResult() == DocWriteResponse.Result.UPDATED)) {
                LOG.info("Index request successful.");
                return true;
            } else {
                LOG.error("Index request failed.");
                return false;
            }

        } catch (Exception e) {
            LOG.error("Error indexing SMS request.");
            return false;
        }


    }


    public SmsRequestIndex getById(String id) {
        try {
            LOG.info("Fetching SMS request by ID: {}", id);
            final GetResponse documentFields = client.get(
                    new GetRequest(Indices.SMS_INDEX, id),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                LOG.warn("SMS request with ID {} not found", id);
                return null;
            }
            SmsRequestIndex smsRequestIndex = MAPPER.readValue(documentFields.getSourceAsString(), SmsRequestIndex.class);
            LOG.info("SMS request with ID {} fetched successfully", id);
            return smsRequestIndex;
        } catch (final Exception e) {
            LOG.error("Error fetching SMS request with ID " + id, e);
            return null;
        }
    }

}
