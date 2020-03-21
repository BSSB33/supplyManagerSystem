package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Suite.SuiteClasses({})
@FixMethodOrder
public class HistoryControllerTest {

    final static String historyJSONPath = "src/test/input/histories/";
    private static TestUtils testUtils = new TestUtils();

    private void assertEqualJSONHistoryToJSONObject(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(historyJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    private void assertEqualJSONHistoryArrayToJSONArray(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(historyJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    //Get All Endpoint
    @Test
    public void givenAdminUser_whenGetAllEndpointIsCalled_thenAllTheHistoriesShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONHistoryArrayToJSONArray(getRequest, "allHistories.json");
    }

    @Test
    public void givenDirectorOrManagerUser_whenGetAllEndpointIsCalled_thenAllTheHistoriesCreatedByTheUserShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONHistoryArrayToJSONArray(getRequest, "historiesCreatedByBalazs.json");
    }

    @Test
    public void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories", "invalisUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get By Id Endpoint
    @Test
    public void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedHistoryShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("histories/1", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("histories/2", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        assertEqualJSONHistoryToJSONObject(getRequest1, "history1.json");
        assertEqualJSONHistoryToJSONObject(getRequest2, "history2.json");
    }

    @Test
    public void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_thenTheRequestedHistoryShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("histories/2", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("histories/4", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        assertEqualJSONHistoryToJSONObject(getRequest1, "company2.json");
        assertEqualJSONHistoryToJSONObject(getRequest2, "company4.json");
    }

    @Test
    public void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories/1", "invalisUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //My History Endpoint
    @Test
    public void givenAnyExistingUser_whenGetHistoryOfUserEndpointIsCalled_thenTheRequestedHistoryShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("histories/mycompany", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONHistoryToJSONObject(getRequest, "company4.json");
    }

    @Test
    public void givenUserWithoutHistory_whenGetHistoryOfUserEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException, JSONException {
        HttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(historyJSONPath + "newManager.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        HttpResponse getRequest = testUtils.sendGetRequest("histories/mycompany", "NewManager:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
        testUtils.sendDeleteRequest("users/11", "Gabor:password");
    }

    //Delete History Endpoint
    @Test
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheHistoryIsDeletable_thenTheRequestedHistoryShouldBeDeleted() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/5", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheHistoryIsNotDeletable_thenNotAcceptableShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/2", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void givenNonAdminUser_whenDeleteByIdEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/3", "Emma:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingHistory_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/10", "Emma:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteRequest.getStatusLine().getStatusCode());
    }

    //Register History Endpoint
    @Test
    public void givenAdminUser_whenRegisterHistoryEndpointIsCalled_thenTheHistoryShouldBeRegistered() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("histories/register", "Gabor:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistory.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("histories/6", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void givenNonAdminUser_whenRegisterHistoryEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("histories/register", "Emma:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistory.json"));
        assertEquals(HttpStatus.SC_FORBIDDEN, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void givenInvalidUser_whenRegisterHistoryEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("histories/register", "invalidUser:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistory.json"));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postRequest.getStatusLine().getStatusCode());
    }

    //Put History Endpoint
    @Test
    public void givenAdminUser_whenUpdateHistoryEndpointIsCalled_thenTheHistoryShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("histories/4", "Gabor:password",
                "{\"name\": \"Renamed Kft.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        HttpResponse putRequest2 = testUtils.sendPutRequest("histories/4", "Gabor:password",
                "{\"name\": \"ELTE-Soft Kft.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    public void givenDirectorUser_whenUpdateHistoryEndpointIsCalled_ifTheHistoryIsHis_thenTheHistoryShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("histories/1", "Balazs:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        HttpResponse putRequest2 = testUtils.sendPutRequest("histories/1", "Balazs:password",
                "{\"name\": \"TelnetWork Kft.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    public void givenDirectorUser_whenUpdateHistoryEndpointIsCalled_ifTheHistoryIsNotHis_thenTheHistoryShouldNotBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("histories/2", "Balazs:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    public void givenManagerUser_whenUpdateHistoryEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("histories/1", "Emma:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    public void givenInvalidUser_whenUpdateHistoryEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("histories/2", "invalidUser:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putRequest1.getStatusLine().getStatusCode());
    }
}