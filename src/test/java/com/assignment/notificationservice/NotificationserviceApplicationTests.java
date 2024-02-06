package com.assignment.notificationservice;

import com.assignment.notificationservice.elasticsearch.SearchRequestDTO;
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

		// Mock GetResponse
		GetResponse getResponseMock = mock(GetResponse.class);
		when(getResponseMock.getSourceAsString()).thenReturn(sourceJson);

		// Mock client.get method
		when(client.get(any(GetRequest.class), any())).thenReturn(getResponseMock);

		// Call the method to be tested
		SmsRequestIndex result = smsRequestIndexService.getById(id);

		// Verify the result
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
		// Mock data
		String id = "2";

		// Mock GetResponse with empty source
		GetResponse getResponseMock = mock(GetResponse.class);
		when(getResponseMock.isSourceEmpty()).thenReturn(true);

		// Mock client.get method
		when(client.get(any(GetRequest.class), any())).thenReturn(getResponseMock);

		// Call the method to be tested
		SmsRequestIndex result = smsRequestIndexService.getById(id);

		// Verify the result
		assertNull(result);
	}

	@Test
	public void testGetById_Exception() throws IOException {
		// Mock data
		String id = "3";

		// Mock client.get method to throw IOException
		when(client.get(any(GetRequest.class), any())).thenThrow(IOException.class);

		// Call the method to be tested
		SmsRequestIndex result = smsRequestIndexService.getById(id);

		// Verify the result
		assertNull(result);
	}



	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// these re the unit tests for indexing:
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testIndex_Success() throws IOException {
		// Mock data
		SmsRequestIndex smsRequestIndex = new SmsRequestIndex("1", "2222222222", "message", "status", "12", "failureComments", new Date(2002, 1, 2), new Date(2002, 1, 2));

		// Mock response
		IndexResponse indexResponseMock = mock(IndexResponse.class);
		when(indexResponseMock.status()).thenReturn(RestStatus.CREATED);

		// Mock client.index method
		when(client.index(any(IndexRequest.class), any())).thenReturn(indexResponseMock);

		// Call the method to be tested
		boolean result = smsRequestIndexService.index(smsRequestIndex);

		// Verify the result
		assertTrue(result);
	}

	@Test
	public void testIndex_Failure() throws IOException {
		// Mock data
		SmsRequestIndex smsRequestIndex = null; // Invalid object

		// Call the method to be tested
		boolean result = smsRequestIndexService.index(smsRequestIndex);

		// Verify the result
		assertFalse(result);
	}

	@Test
	public void testIndex_Exception() throws IOException {
		// Mock data
		SmsRequestIndex smsRequestIndex = new SmsRequestIndex("1", "2222222222", "message", "status", "12", "failureComments", new Date(2002, 1, 2), new Date(2002, 1, 2));

		// Mock client.index method to throw IOException
		when(client.index(any(IndexRequest.class), any())).thenThrow(IOException.class);

		// Call the method to be tested
		boolean result = smsRequestIndexService.index(smsRequestIndex);

		// Verify the result
		assertFalse(result);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//these are for searchCreatedsince
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testSearchCreatedSince_Success() throws IOException {
		// Mock data
		SearchRequestDTO dto = new SearchRequestDTO(/* provide necessary arguments */);
		Date date = new Date(/* provide necessary arguments */);

		// Mock response
		SearchResponse searchResponseMock = mock(SearchResponse.class);
		SearchHits searchHitsMock = mock(SearchHits.class);
		when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
		when(searchHitsMock.getHits()).thenReturn(new SearchHit[] {}); // Provide mock hits if needed

		// Mock client.search method
		when(client.search(any(SearchRequest.class), any())).thenReturn(searchResponseMock);

		// Call the method to be tested
		List<SmsRequestIndex> result = smsRequestIndexService.searchCreatedSince(dto, date);

		// Verify the result
		assertNotNull(result);
 	}

	@Test
	public void testSearchCreatedSince_NullRequest() throws IOException {
		// Mock data
		SearchRequestDTO dto = null;
		Date date = new Date(/* provide necessary arguments */);

		// Call the method to be tested
		List<SmsRequestIndex> result = smsRequestIndexService.searchCreatedSince(dto, date);

		// Verify the result
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

}
