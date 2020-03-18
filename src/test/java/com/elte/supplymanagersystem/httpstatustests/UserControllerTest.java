package com.elte.supplymanagersystem.httpstatustests;

import com.elte.supplymanagersystem.TestUtils;
import com.elte.supplymanagersystem.repositories.UserRepository;
import com.elte.supplymanagersystem.services.UserService;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    //givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private TestUtils testUtils = new TestUtils();

    @Test
    public void testGetAllEndpointWithAdmin() throws IOException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void testGetAllEndpointWithDirector() throws IOException {
        CloseableHttpResponse getRequest1 = testUtils.sendGetRequest("users", "Balazs:password");
        CloseableHttpResponse getRequest2 = testUtils.sendGetRequest("users", "Judit:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getRequest2.getStatusLine().getStatusCode());
    }

    @Test
    public void testGetAllEndpointWithManager() throws IOException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Emma:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void testGetAllEndpointWithDisabledUser() throws IOException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "Old Student:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void testGetAllEndpointWithIvalidUser() throws IOException, JSONException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "invalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void get() {
    }

    @Test
    void getUnassignedDirectors() {
    }

    @Test
    void put() {
    }

    @Test
    void delete() {
    }

    @Test
    void register() {
    }
}