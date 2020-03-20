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

    final static String userJSONPath = "src/test/input/users/";
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
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllUsersShouldBeReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());

        assertEqualJSONUserArrayToJSONArray(getRequest, "allUsers.json");
    }

    @Test
    void givenDirectorUser_whenGetAllEndpointIsCalled_thenAllEmployeesShouldBeReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users", "Balazs:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users", "Judit:password");

        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        assertEqualJSONUserArrayToJSONArray(getRequest1, "colleaguesOfBalazs.json");
        assertEqualJSONUserArrayToJSONArray(getRequest2, "colleaguesOfJudit.json");
    }

    @Test
    void givenManagerUser_whenGetAllEndpointIsCalled_thenAllColleaguesShouldBeReturned() throws IOException, JSONException {
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
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users/1", "Gabor:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users/2", "Gabor:password");
        CloseableHttpResponse getRequest3 = testUtils.sendGetRequest("users/3", "Gabor:password");
        CloseableHttpResponse getRequest4 = testUtils.sendGetRequest("users/5", "Gabor:password");

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
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/10", "Gabor:password");

        assertEquals(HttpStatus.SC_NOT_FOUND, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForAnEmployee_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/5", "Balazs:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userEmma.json");
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForItself_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/2", "Balazs:password");

        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest, "userBalazs.json");
    }

    @Test
    void givenManagerUser_whenGetByIdEndpointIsCalledForColleague_thenTheRequestedUserShouldBeReturned() throws IOException, JSONException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users/2", "Emma:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users/4", "TTManager:password");

        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
        assertEqualJSONUserToJSONObject(getRequest1, "userBalazs.json");
        assertEqualJSONUserToJSONObject(getRequest2, "userGyuri.json");
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForAUserFromAnOtherCompany_thenUnauthorizedShouldBeThrown() throws IOException, JSONException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users/1", "Balazs:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users/7", "Balazs:password");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalledForExistingUser_thenForbiddenShouldBeThrown() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/2", "invalidUser:password");

        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalledForNonExistingUser_thenForbiddenShouldBeThrown() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/20", "invalidUser:password");

        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
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