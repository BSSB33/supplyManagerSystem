package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.Suite;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FixMethodOrder
class UserControllerTest {

    final static String userJSONPath = "src/test/input/users/";
    private static TestUtils testUtils = new TestUtils();

    void assertEqualJSONUserToJSONObject(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(userJSONPath + expectedJSONPath)).toString(),
                testUtils.getUserComparator());
    }

    void assertEqualJSONUserArrayToJSONArray(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(userJSONPath + expectedJSONPath)).toString(),
                testUtils.getUserComparator());
    }

    //Get All Endpoint
    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllUsersShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("users", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONUserArrayToJSONArray(getRequest, "allUsers.json");
    }

    @Test
    void givenDirectorUser_whenGetAllEndpointIsCalled_thenAllEmployeesShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("users", "Balazs:password");
        HttpResponse getRequest2 = testUtils.sendGetRequest("users", "Judit:password");

        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        assertEqualJSONUserArrayToJSONArray(getRequest1, "colleaguesOfBalazs.json");
        assertEqualJSONUserArrayToJSONArray(getRequest2, "colleaguesOfJudit.json");
    }

    @Test
    void givenManagerUser_whenGetAllEndpointIsCalled_thenAllColleaguesShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("users", "Emma:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserArrayToJSONArray(getRequest, "colleaguesOfBalazs.json");
    }

    @Test
    void givenDisabledUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("users", "Old Student:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("users", "invalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/1", "Gabor:password");
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/2", "Gabor:password");
        HttpResponse getRequest3 = testUtils.sendGetRequest("users/3", "Gabor:password");
        HttpResponse getRequest4 = testUtils.sendGetRequest("users/5", "Gabor:password");

        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest3.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest4.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest1, "userGabor.json");
        assertEqualJSONUserToJSONObject(getRequest2, "userBalazs.json");
        assertEqualJSONUserToJSONObject(getRequest3, "userJudit.json");
        assertEqualJSONUserToJSONObject(getRequest4, "userEmma.json");
    }

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalledForNonExistingUserId_thenTheNotFoundShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/10", "Gabor:password");

        assertEquals(HttpStatus.SC_NOT_FOUND, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForAnEmployee_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/5", "Balazs:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userEmma.json");
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForItself_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/2", "Balazs:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userBalazs.json");
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForAUserFromAnOtherCompany_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/1", "Balazs:password");
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/7", "Balazs:password");

        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenGetByIdEndpointIsCalledForColleague_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/2", "Emma:password");
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/4", "TTManager:password");

        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest1, "userBalazs.json");
        assertEqualJSONUserToJSONObject(getRequest2, "userGyuri.json");
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalledForExistingUser_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/2", "invalidUser:password");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalledForNonExistingUser_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/20", "invalidUser:password");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDisabledUser_whenGetByIdEndpointIsCalled_thenFOShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/2", "Old Student:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Delete Endpoint
    @Test
    void givenAdminUser_whenDeleteEndpointIsCalled_thenUserShouldBeDeleted() throws IOException {
        //Adds user
        HttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(userJSONPath + "userPeter.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        //Deletes new user
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteEndpointIsCalled_toDeleteANotDeletableUser_thenNotAcceptableShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/2", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenDeleteEndpointIsCalled_toDeleteAnEmployeeUser_thenNotAcceptableShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/5", "Balazs:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenDeleteEndpointIsCalled_toDeleteAnNonEmployeeUser_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/3", "Balazs:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, deleteRequest.getStatusLine().getStatusCode());
    }

    //Disable Endpoint
    @Test
    void givenAdminUser_whenDisableEndpointIsCalled_toDisableAUser_thenTheUserShouldBeDisabled() throws IOException, JSONException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/3/disable", "Gabor:password", "");
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        assertEquals("false", testUtils.getJsonObjectField(putRequest, "enabled"));
        testUtils.sendPutRequest("users/3/enable", "Gabor:password", "");
    }

    @Test
    void givenDirectorUser_whenDisableEndpointIsCalled_toDisableAnEmployeeUser_thenTheUserShouldBeDisabled() throws IOException, JSONException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/5/disable", "Balazs:password", "");
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        assertEquals("false", testUtils.getJsonObjectField(putRequest, "enabled"));
        testUtils.sendPutRequest("users/5/enable", "Balazs:password", "");
    }

    @Test
    void givenDirectorUser_whenDisableEndpointIsCalled_toDisableANonEmployeeUser_thenTheUserShouldNotBeDisabled() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/4/disable", "Balazs:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenDisableEndpointIsCalled_toDisableAnColleagueUser_thenTheUserShouldNotBeDisabled() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/2/disable", "Emma:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenDisableEndpointIsCalled_toDisableANonEmployeeUser_thenTheUserShouldNotBeDisabled() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/6/disable", "Emma:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    //Enable Endpoint
    @Test
    void givenAdminUser_whenEnableEndpointIsCalled_toEnableAUser_thenTheUserShouldBeEnabled() throws IOException, JSONException {
        //Prepare
        testUtils.sendPutRequest("users/3/disable", "Gabor:password", "");
        //Execute
        HttpResponse putRequest = testUtils.sendPutRequest("users/3/enable", "Gabor:password", "");
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        assertEquals("true", testUtils.getJsonObjectField(putRequest, "enabled"));
    }

    @Test
    void givenDirectorUser_whenEnableEndpointIsCalled_toEnableAnEmployeeUser_thenTheUserShouldBeDEnabled() throws IOException, JSONException {
        //Prepare
        testUtils.sendPutRequest("users/5/disable", "Balazs:password", "");
        //Execute
        HttpResponse putRequest = testUtils.sendPutRequest("users/5/enable", "Balazs:password", "");
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        assertEquals("true", testUtils.getJsonObjectField(putRequest, "enabled"));
    }

    @Test
    void givenDirectorUser_whenEnableEndpointIsCalled_toEnableANonEmployeeUser_thenTheUserShouldNotBeEnabled() throws IOException {
        //Execute
        HttpResponse putRequest = testUtils.sendPutRequest("users/4/enable", "Balazs:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenEnableEndpointIsCalled_toEnableABossUser_thenTheUserShouldNotBeEnabled() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/2/enable", "Emma:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenEnableEndpointIsCalled_toEnableAColleagueUser_thenTheUserShouldNotBeEnabled() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/2/enable", "Emma:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenEnableEndpointIsCalled_toEnableANonEmployeeUser_thenTheUserShouldNotBeEnabled() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/6/enable", "Emma:password", "");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    //Get Unassigned Directors Endpoint
    @Test
    void givenAdminUser_whenGetUnassignedDirectorsEndpointIsCalled_thenTheUsersShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("users/freeDirectors", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserArrayToJSONArray(getRequest, "nonDirectorUsers.json");
    }

    @Test
    void givenNonAdminUser_whenGetUnassignedDirectorsEndpointIsCalled_thenTheUsersShouldNotBeReturned() throws IOException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/freeDirectors", "Balazs:password");
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/freeDirectors", "Emma:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest2.getStatusLine().getStatusCode());
    }

    //Login Endpoint
    @Test
    void givenUser_whenLoginEndpointIsCalled_thenOkShouldBeReturned() throws IOException {
        HttpResponse postRequest1 = testUtils.sendPostRequest("users/login", "Gabor:password", "");
        HttpResponse postRequest2 = testUtils.sendPostRequest("users/login", "Balazs:password", "");
        HttpResponse postRequest3 = testUtils.sendPostRequest("users/login", "Judit:password", "");
        HttpResponse postRequest4 = testUtils.sendPostRequest("users/login", "Emma:password", "");
        assertEquals(HttpStatus.SC_OK, postRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, postRequest2.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, postRequest3.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, postRequest4.getStatusLine().getStatusCode());
    }

    //Register Endpoint
    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withManagerUserToRegister_thenUserShouldBeRegistered() throws IOException, JSONException {
        HttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(userJSONPath + "userPeter.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());

        HttpResponse getRequest = testUtils.sendGetRequest("users/9", "Gabor:password");
        assertEqualJSONUserToJSONObject(getRequest, "userPeterRegistered.json");

        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/9", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withDirectorUserToRegister_thenUserShouldBeRegistered_andCompanyShouldBeSet() throws IOException, JSONException {
        HttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(userJSONPath + "userNewDirector.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());

        HttpResponse getRequest = testUtils.sendGetRequest("users/10", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userNewDirectorRegistered.json");

        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/10", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withExistingUserToRegister_thenBadRequestShouldBeThrown() throws IOException, JSONException {
        HttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(userJSONPath + "userBalazs.json"));
        assertEquals(HttpStatus.SC_BAD_REQUEST, postRequest.getStatusLine().getStatusCode());
    }

    //Put By ID Endpoint
    @Test
    void givenAnyUser_whenPutByIdEndpointIsCalled_withNonExistingUser_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/20", "Emma:password", "{\"id\":20}");
        assertEquals(HttpStatus.SC_NOT_FOUND, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAnyUser_whenPutByIdEndpointIsCalled_withEmptyUserBodey_thenBadRequestShouldBeThrown() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/20", "Emma:password", "");
        assertEquals(HttpStatus.SC_BAD_REQUEST, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenPutByIdEndpointIsCalled_thenTheRequestedUserShouldBeUpdated() throws IOException, JSONException {
        //Getting User by ID
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/3", "Gabor:password");
        JSONObject jsonObject = testUtils.getJsonObject(getRequest1);
        jsonObject.put("username", "Judit2");
        //Execute
        HttpResponse putRequest = testUtils.sendPutRequest("users/3", "Gabor:password", jsonObject.toString());
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        //Check
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/3", "Gabor:password");
        String nameOfUser = testUtils.getJsonObject(getRequest2).getString("username");
        assertEquals("Judit2", nameOfUser);
        //Restore
        jsonObject.put("username", "Judit");
        HttpResponse putRequest2 = testUtils.sendPutRequest("users/3", "Gabor:password", jsonObject.toString());
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenPutByIdEndpointIsCalled_withADirectorWithoutWorkplace_thenTheRequestedUserShouldBeUpdatedAndCompanyShouldAppearAtWorkplaceField() throws IOException, JSONException {
        //Getting User by ID
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/2", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        JSONObject jsonObject = testUtils.getJsonObject(getRequest1);
        jsonObject.put("workplace", null);
        //Execute
        HttpResponse putRequest = testUtils.sendPutRequest("users/2", "Gabor:password", jsonObject.toString());
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());

        HttpResponse getRequest2 = testUtils.sendGetRequest("users/2", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        String workplaceOfUser = testUtils.getJsonObject(getRequest2).getString("workplace");
        JSONAssert.assertEquals("{\"id\": 1,\"name\": \"TelnetWork Kft.\"}", workplaceOfUser, JSONCompareMode.LENIENT);
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toModifyAnEmployeeFromItsCompany_thenTheRequestedUserShouldBeUpdated() throws IOException, JSONException {
        //Get Employee
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/5", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        JSONObject employee = testUtils.getJsonObject(getRequest1);
        employee.put("username", "Employee");
        //Rename Employee
        HttpResponse putRequest = testUtils.sendPutRequest("users/5", "Balazs:password", employee.toString());
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        //GetEmployee again
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/5", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        JSONObject modifiedEmployee = testUtils.getJsonObject(getRequest2);
        assertEquals("Employee", modifiedEmployee.getString("username"));

        //Restore original Object
        employee.put("username", "Emma");
        HttpResponse putRequest2 = testUtils.sendPutRequest("users/5", "Balazs:password", employee.toString());
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toModifyItself_thenTheUserShouldBeUpdated() throws IOException, JSONException {
        //Get Director
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/2", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        JSONObject director = testUtils.getJsonObject(getRequest1);
        ;
        director.put("username", "Balazs2");
        //Rename Director
        HttpResponse putRequest = testUtils.sendPutRequest("users/2", "Balazs:password", director.toString());
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        //GetDirector again
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/2", "Balazs2:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        JSONObject modifiedEmployee = testUtils.getJsonObject(getRequest2);
        assertEquals("Balazs2", modifiedEmployee.getString("username"));
        //Restore original Object
        director.put("username", "Balazs");
        HttpResponse putRequest2 = testUtils.sendPutRequest("users/2", "Balazs2:password", director.toString());
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toModifyAManagerWhoWorksSomeWhereElse_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/6", "Balazs:password", "{\"username\":\"OtherWorker\"}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenPutByIdEndpointIsCalled_toModifyItself_thenTheUserShouldBeUpdated() throws IOException, JSONException {
        //Get Director
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/5", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        JSONObject director = testUtils.getJsonObject(getRequest1);
        ;
        director.put("username", "Emma2");
        //Rename Director
        HttpResponse putRequest = testUtils.sendPutRequest("users/5", "Emma:password", director.toString());
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        //GetDirector again
        HttpResponse getRequest2 = testUtils.sendGetRequest("users/5", "Emma2:password");
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        JSONObject modifiedEmployee = testUtils.getJsonObject(getRequest2);
        assertEquals("Emma2", modifiedEmployee.getString("username"));
        //Restore original Object
        director.put("username", "Emma");
        HttpResponse putRequest2 = testUtils.sendPutRequest("users/5", "Emma2:password", director.toString());
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenPutByIdEndpointIsCalled_toModifyColleague_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse putRequest = testUtils.sendPutRequest("users/2", "Emma:password", "{\"username\":\"OtherWorker\"}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest.getStatusLine().getStatusCode());
    }

    @Test
    @AfterAll
    static void givenAdminUser_whenPutByIdEndpointIsCalled_withAUserConnectedWithOtherObjects_afterRemovingConnections_thenTheRequestedUserShouldBeDeleted() throws IOException, JSONException {
        //Get User by ID
        HttpResponse getRequest1 = testUtils.sendGetRequest("users/6", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        JSONObject jsonObject = testUtils.getJsonObject(getRequest1);
        //Try To Delete
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("users/6", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
        //Remove all connections from user side
        jsonObject.put("workplace", null);
        HttpResponse putRequest = testUtils.sendPutRequest("users/6", "Gabor:password", jsonObject.toString());
        assertEquals(HttpStatus.SC_OK, putRequest.getStatusLine().getStatusCode());
        //Put History
        HttpResponse putRequest2 = testUtils.sendDeleteRequest("histories/9", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
        HttpResponse putRequest3 = testUtils.sendDeleteRequest("histories/5", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, putRequest3.getStatusLine().getStatusCode());
        HttpResponse putRequest4 = testUtils.sendDeleteRequest("histories/4", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, putRequest4.getStatusLine().getStatusCode());
        //Get Orders
        HttpResponse getRequest6 = testUtils.sendGetRequest("orders/1", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest6.getStatusLine().getStatusCode());
        JSONObject order1 = testUtils.getJsonObject(getRequest6);
        order1.put("buyerManager", null);
        HttpResponse getRequest5 = testUtils.sendGetRequest("orders/2", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest5.getStatusLine().getStatusCode());
        JSONObject order2 = testUtils.getJsonObject(getRequest5);
        order2.put("buyerManager", null);
        //Put Orders
        HttpResponse putRequest6 = testUtils.sendPutRequest("orders/1", "Gabor:password", order1.toString());
        assertEquals(HttpStatus.SC_OK, putRequest6.getStatusLine().getStatusCode());
        HttpResponse putRequest5 = testUtils.sendPutRequest("orders/2", "Gabor:password", order2.toString());
        assertEquals(HttpStatus.SC_OK, putRequest5.getStatusLine().getStatusCode());
        //Remove User
        HttpResponse deleteRequest2 = testUtils.sendDeleteRequest("users/6", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest2.getStatusLine().getStatusCode());
    }
}