package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder
public class HistoryControllerTest {

    final static String historyJSONPath = "src/test/input/histories/";
    private static TestUtils testUtils = new TestUtils();

    private void assertEqualJSONHistoryToJSONObject(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(historyJSONPath + expectedJSONPath)).toString(),
                testUtils.getHistoryComparator());
    }

    private void assertEqualJSONHistoryArrayToJSONArray(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(historyJSONPath + expectedJSONPath)).toString(),
                testUtils.getHistoryComparator());
    }

    //Get All Endpoint
    @Test
    @Order(1)
    public void givenAdminUser_whenGetAllEndpointIsCalled_thenAllTheHistoriesShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        //TODO try to fix content check
    }

    @Test
    @Order(2)
    public void givenDirectorOrManagerUser_whenGetAllEndpointIsCalled_thenAllTheHistoriesCreatedByTheUserShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    public void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories", "invalisUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get By Id Endpoint
    @Test
    @Order(4)
    public void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedHistoryShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("histories/1", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("histories/2", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        assertEqualJSONHistoryToJSONObject(getRequest1, "history1.json");
        assertEqualJSONHistoryToJSONObject(getRequest2, "history2.json");
    }

    @Test
    @Order(5)
    public void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheCreatorWorksAtTheSameCompany_thenTheRequestedHistoryShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("histories/1", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("histories/2", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        assertEqualJSONHistoryToJSONObject(getRequest1, "history1.json");
        assertEqualJSONHistoryToJSONObject(getRequest2, "history2.json");
    }

    @Test
    @Order(5)
    public void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheCreatorDoNotWorksAtTheSameCompany_thenTheRequestedHistoryShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("histories/12", "Balazs:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("histories/13", "Emma:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest2.getStatusLine().getStatusCode());
    }

    @Test
    @Order(6)
    public void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories/1", "invalisUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }
    //TODO history access check to different users
    //Delete History Endpoint
    @Test
    @Order(7)
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheHistoryIsDeletable_thenTheRequestedHistoryShouldBeDeleted() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    @Order(8)
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingHistory_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/100", "Emma:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteRequest.getStatusLine().getStatusCode());
    }

    //Add History Endpoint
    @Test
    @Order(9)
    public void givenAdminUser_whenAddHistoryEndpointIsCalled_thenTheHistoryShouldBeAdded() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("histories", "Gabor:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistory.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/16", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    @Order(10)
    public void givenManagerUser_whenAddHistoryEndpointIsCalled_thenTheHistoryShouldBeAdded() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("histories", "Emma:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistoryByManager.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/1", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    @Order(11)
    public void givenInvalidUser_whenRegisterHistoryEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("histories", "invalidUser:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistory.json"));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postRequest.getStatusLine().getStatusCode());
    }
}