package com.elte.supplymanagersystem.controllers;

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
    //System.out.println(getJSONField(httpResponse, 0, "username")); //Gabor
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private TestUtils testUtils = new TestUtils();

    //TODO make these separate Tests
    @Test
    public void testGetAllEndpointWithDifferentUsers() throws IOException, JSONException {
        CloseableHttpResponse adminGetRequest = testUtils.sendGetRequest("users", "Gabor:password");
        CloseableHttpResponse managerGetRequest1 = testUtils.sendGetRequest("users", "Balazs:password");
        CloseableHttpResponse managerGetRequest2 = testUtils.sendGetRequest("users", "Judit:password");
        CloseableHttpResponse disabledUserResponse = testUtils.sendGetRequest("users", "Old Student:password");
        CloseableHttpResponse invalidUserResponse = testUtils.sendGetRequest("users", "invalidUser:password");
        assertEquals(HttpStatus.SC_OK, adminGetRequest.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, managerGetRequest1.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, managerGetRequest2.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_FORBIDDEN, disabledUserResponse.getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_UNAUTHORIZED, invalidUserResponse.getStatusLine().getStatusCode());

        JSONArray jsonArray1 = testUtils.getJsonArray(adminGetRequest);
        assertEquals(jsonArray1.length(), 7);

        JSONArray jsonArray2 = testUtils.getJsonArray(managerGetRequest1);
        assertEquals(jsonArray2.length(), 2);

        JSONArray jsonArray3 = testUtils.getJsonArray(managerGetRequest2);
        assertEquals(jsonArray3.length(), 1);

    }

//    @Test
//    void get() {
//    }
//
//    @Test
//    void getUnassignedDirectors() {
//    }
//
//    @Test
//    void put() {
//    }
//
//    @Test
//    void delete() {
//    }
//
//    @Test
//    void register() {
//    }
}