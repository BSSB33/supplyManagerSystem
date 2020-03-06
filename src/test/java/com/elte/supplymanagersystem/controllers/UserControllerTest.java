package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.services.UserService;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

import static com.elte.supplymanagersystem.TestUtils.getJSONField;
import static com.elte.supplymanagersystem.TestUtils.getRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    //    givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived
    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void getAll() throws IOException, JSONException {
        CloseableHttpResponse httpResponse = getRequest("users", "Gabor:password");
        //System.out.println(getJSONField(httpResponse, 0, "username")); //Gabor

        //TESTS
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    @AfterEach
    void tearDown() {
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