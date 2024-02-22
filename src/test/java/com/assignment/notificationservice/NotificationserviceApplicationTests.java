package com.assignment.notificationservice;

import com.assignment.notificationservice.dto.requestDTO.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
@SpringBootTest
class NotificationserviceApplicationTests {

	@Test
	void contextLoads() {
	}
	@Mock
	private RestHighLevelClient client;

	private ElasticSearchSmsService smsRequestIndexService;
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		objectMapper = new ObjectMapper();
		smsRequestIndexService = new ElasticSearchSmsService(client);
	}

	@Test
	public void testGetById_Success() throws IOException {
		// Mock data
		String id = "1";
		SmsRequestIndex expectedSmsRequest = new SmsRequestIndex(id, "2222222222", "message", "status", "12", "failureComments", new Date(101, 1, 2), new Date(101, 1, 2));
		String sourceJson = "{\"id\":\"1\",\"phoneNumber\":\"2222222222\",\"message\":\"message\",\"status\":\"status\",\"failureCode\":\"12\",\"failureComments\":\"failureComments\",\"createdAt\":\"2002-02-02\",\"updatedAt\":\"2002-02-02\"}";

		GetResponse getResponseMock = mock(GetResponse.class);
		when(getResponseMock.getSourceAsString()).thenReturn(sourceJson);
		when(client.get(any(GetRequest.class), any())).thenReturn(getResponseMock);

		SmsRequestIndex result = smsRequestIndexService.getById(id);

		assertNotNull(result);
		assertEquals(expectedSmsRequest.getId(), result.getId());
		assertEquals(expectedSmsRequest.getPhoneNumber(), result.getPhoneNumber());
		assertEquals(expectedSmsRequest.getMessage(), result.getMessage());
		assertEquals(expectedSmsRequest.getStatus(), result.getStatus());
		assertEquals(expectedSmsRequest.getFailureCode(), result.getFailureCode());
		assertEquals(expectedSmsRequest.getFailureComments(), result.getFailureComments());

	}

	@Test
	public void testGetById_NotFound() throws IOException {
		String id = "2";

		GetResponse getResponseMock = mock(GetResponse.class);
		when(getResponseMock.isSourceEmpty()).thenReturn(true);
		when(client.get(any(GetRequest.class), any())).thenReturn(getResponseMock);

		SmsRequestIndex result = smsRequestIndexService.getById(id);

		assertNull(result);
	}

	@Test
	public void testGetById_Exception() throws IOException {
		String id = "3";

		when(client.get(any(GetRequest.class), any())).thenThrow(IOException.class);
		SmsRequestIndex result = smsRequestIndexService.getById(id);

		assertNull(result);
	}


	@Test
	public void testIndex_Success() throws Exception {
		SmsRequestIndex smsRequestIndex = new SmsRequestIndex("1", "2222222222", "message", "status", "12", "failureComments", new Date(2002, 1, 2), new Date(2002, 1, 2));

		IndexResponse indexResponseMock = mock(IndexResponse.class);
		when(indexResponseMock.status()).thenReturn(RestStatus.CREATED);

		when(client.index(any(IndexRequest.class), any())).thenReturn(indexResponseMock);

		boolean result = smsRequestIndexService.index(smsRequestIndex);

		assertTrue(result);
	}

	@Test
	public void testIndex_Failure() throws Exception {
		SmsRequestIndex smsRequestIndex = null;

		boolean result = smsRequestIndexService.index(smsRequestIndex);

		assertFalse(result);
	}

	@Test
	public void testIndex_Exception() throws Exception {
		SmsRequestIndex smsRequestIndex = new SmsRequestIndex("1", "2222222222", "message", "status", "12", "failureComments", new Date(2002, 1, 2), new Date(2002, 1, 2));

		when(client.index(any(IndexRequest.class), any())).thenThrow(IOException.class);

		boolean result = smsRequestIndexService.index(smsRequestIndex);

		assertFalse(result);
	}
}
