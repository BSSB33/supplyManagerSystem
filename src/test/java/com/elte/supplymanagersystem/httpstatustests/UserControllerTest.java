package com.elte.supplymanagersystem.httpstatustests;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FixMethodOrder
class UserControllerTest {


    private TestUtils testUtils = new TestUtils();

    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllUsersReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        String expected = testUtils.getContentOfFile("src/test/input/allUsers.json");
        JSONAssert.assertEquals(testUtils.getJsonArray(getRequest).toString(), new JSONArray(expected).toString(),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
    }

    @Test
    void givenDirectorUser_whenGetAllEndpointIsCalled_thenAllEmployeesAreReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users", "Balazs:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users", "Judit:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());

        String expected1 = testUtils.getContentOfFile("src/test/input/colleaguesOfBalazs.json");
        JSONAssert.assertEquals(testUtils.getJsonArray(getRequest1).toString(), new JSONArray(expected1).toString(),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
        String expected2 = testUtils.getContentOfFile("src/test/input/colleaguesOfJudit.json");
        JSONAssert.assertEquals(testUtils.getJsonArray(getRequest2).toString(), new JSONArray(expected2).toString(),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
    }

    @Test
    void givenManagerUser_whenGetAllEndpointIsCalled_thenAllColleaguesAreReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        String expected = testUtils.getContentOfFile("src/test/input/colleaguesOfBalazs.json");
        JSONAssert.assertEquals(testUtils.getJsonArray(getRequest).toString(), new JSONArray(expected).toString(),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
    }

    @Test
    void givenDisabledUser_whenGetAllEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Old Student:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "invalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

//    @Test
//    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedUserIsReturned() {
//
//    }

    @Test
    void givenAdminUser_whenDeleteEndpointIsCalled_thenUserShouldBeDeleted() throws IOException {
        //Adds user
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile("src/test/input/userPeter.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        //Deletes new user
        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withManagerUserToRegister_thenUserShouldBeRegistered() throws IOException, JSONException {
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile("src/test/input/userPeter.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/9", "Gabor:password");
        JSONAssert.assertEquals(testUtils.getJsonObject(getRequest).toString(),
                testUtils.getContentOfFile("src/test/input/userPeterRegistered.json"),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/9", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withDirectorUserToRegister_thenUserShouldBeRegistered_andCompanyShouldBeSet() throws IOException, JSONException {
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile("src/test/input/userNewDirector.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/10", "Gabor:password");
        JSONAssert.assertEquals(testUtils.getJsonObject(getRequest).toString(),
                testUtils.getContentOfFile("src/test/input/userNewDirectorRegistered.json"),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/10", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withExistingUserToRegister_thenBadRequestShouldBeThrown() throws IOException, JSONException {
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile("src/test/input/userBalazs.json"));
        assertEquals(HttpStatus.SC_BAD_REQUEST, postRequest.getStatusLine().getStatusCode());
    }

}