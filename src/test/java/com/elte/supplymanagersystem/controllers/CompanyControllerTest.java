package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.Suite;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.annotation.Order;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FixMethodOrder
class CompanyControllerTest {

    final static String companyJSONPath = "src/test/input/companies/";
    private static TestUtils testUtils = new TestUtils();

    private void assertEqualJSONCompanyToJSONObject(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(companyJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    private void assertEqualJSONCompanyArrayToJSONArray(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(companyJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }


    //Get All Endpoint
    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllTheCompaniesShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("companies", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONCompanyArrayToJSONArray(getRequest, "allCompanies.json");
    }

    @Test
    void givenDirectorOrManagerUser_whenGetAllEndpointIsCalled_thenAllTheCompaniesShouldBeReturned() throws IOException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("companies", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("companies", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("companies", "invalisUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedCompanyShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("companies/1", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("companies/3", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        assertEqualJSONCompanyToJSONObject(getRequest1, "company1.json");
        assertEqualJSONCompanyToJSONObject(getRequest2, "company3.json");
    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_thenTheRequestedCompanyShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("companies/2", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        HttpResponse getRequest2 = testUtils.sendGetRequest("companies/4", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        assertEqualJSONCompanyToJSONObject(getRequest1, "company2.json");
        assertEqualJSONCompanyToJSONObject(getRequest2, "company4.json");
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("companies/1", "invalisUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //My Company Endpoint
    @Test
    void givenAnyExistingUser_whenGetCompanyOfUserEndpointIsCalled_thenTheRequestedCompanyShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("companies/mycompany", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONCompanyToJSONObject(getRequest, "company4.json");
    }

    @Test
    void givenUserWithoutCompany_whenGetCompanyOfUserEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException, JSONException {
        HttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(companyJSONPath + "newManager.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        HttpResponse getRequest = testUtils.sendGetRequest("companies/mycompany", "NewManager:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
        testUtils.sendDeleteRequest("users/11", "Gabor:password");
    }

    //Delete Company Endpoint
    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheCompanyIsDeletable_thenTheRequestedCompanyShouldBeDeleted() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("companies/5", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheCompanyIsNotDeletable_thenNotAcceptableShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("companies/2", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenNonAdminUser_whenDeleteByIdEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("companies/3", "Emma:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingCompany_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("companies/10", "Emma:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteRequest.getStatusLine().getStatusCode());
    }

    //Register Company Endpoint
    @Test
    void givenAdminUser_whenRegisterCompanyEndpointIsCalled_thenTheCompanyShouldBeRegistered() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("companies/register", "Gabor:password",
                testUtils.getContentOfFile(companyJSONPath + "newCompany.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("companies/6", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenNonAdminUser_whenRegisterCompanyEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("companies/register", "Emma:password",
                testUtils.getContentOfFile(companyJSONPath + "newCompany.json"));
        assertEquals(HttpStatus.SC_FORBIDDEN, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenRegisterCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("companies/register", "invalidUser:password",
                testUtils.getContentOfFile(companyJSONPath + "newCompany.json"));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postRequest.getStatusLine().getStatusCode());
    }

    //Put Company Endpoint
    @Test
    void givenAdminUser_whenUpdateCompanyEndpointIsCalled_thenTheCompanyShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/4", "Gabor:password",
                "{\"name\": \"Renamed Kft.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        HttpResponse putRequest2 = testUtils.sendPutRequest("companies/4", "Gabor:password",
                "{\"name\": \"ELTE-Soft Kft.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenUpdateCompanyEndpointIsCalled_ifTheCompanyIsHis_thenTheCompanyShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/1", "Balazs:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        HttpResponse putRequest2 = testUtils.sendPutRequest("companies/1", "Balazs:password",
                "{\"name\": \"TelnetWork Kft.\"}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenUpdateCompanyEndpointIsCalled_ifTheCompanyIsNotHis_thenTheCompanyShouldNotBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/2", "Balazs:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenUpdateCompanyEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/1", "Emma:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenUpdateCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/2", "invalidUser:password",
                "{\"name\": \"TelnetWork Bt.\"}");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putRequest1.getStatusLine().getStatusCode());
    }
}