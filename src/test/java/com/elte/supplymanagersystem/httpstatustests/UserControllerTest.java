package com.elte.supplymanagersystem.httpstatustests;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    final static String userJSONPath = "src/test/input/";
    private TestUtils testUtils = new TestUtils();


    private void assertEqualJSONUserToJSONObject(CloseableHttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(userJSONPath + expectedJSONPath)).toString(),
                testUtils.getUserComparator());
    }

    private void assertEqualJSONUserArrayToJSONArray(CloseableHttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(userJSONPath + expectedJSONPath)).toString(),
                testUtils.getUserComparator());
    }

    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllUsersReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONUserArrayToJSONArray(getRequest, "allUsers.json");
    }

    @Test
    void givenDirectorUser_whenGetAllEndpointIsCalled_thenAllEmployeesAreReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users", "Balazs:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users", "Judit:password");

        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        assertEqualJSONUserArrayToJSONArray(getRequest1, "colleaguesOfBalazs.json");
        assertEqualJSONUserArrayToJSONArray(getRequest2, "colleaguesOfJudit.json");
    }

    @Test
    void givenManagerUser_whenGetAllEndpointIsCalled_thenAllColleaguesAreReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Emma:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserArrayToJSONArray(getRequest, "colleaguesOfBalazs.json");
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

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedUserIsReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/1", "Gabor:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userGabor.json");
    }


    @Test
    void givenAdminUser_whenDeleteEndpointIsCalled_thenUserShouldBeDeleted() throws IOException {
        //Adds user
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(userJSONPath + "userPeter.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        //Deletes new user
        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withManagerUserToRegister_thenUserShouldBeRegistered() throws IOException, JSONException {
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile(userJSONPath + "userPeter.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());

        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/9", "Gabor:password");
        assertEqualJSONUserToJSONObject(getRequest, "userPeterRegistered.json");

        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/9", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withDirectorUserToRegister_thenUserShouldBeRegistered_andCompanyShouldBeSet() throws IOException, JSONException {
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password",
                testUtils.getContentOfFile("src/test/input/userNewDirector.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());

        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/10", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userNewDirectorRegistered.json");

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