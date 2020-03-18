package com.elte.supplymanagersystem.httpstatustests;

import com.elte.supplymanagersystem.TestUtils;
import com.elte.supplymanagersystem.repositories.UserRepository;
import com.elte.supplymanagersystem.services.UserService;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestPropertySource(locations="classpath:application.properties")
class UserControllerTest {

//    @BeforeEach
//    public void resetDb(){
//
//    }

    //Example: givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived

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
    public void testGetAllEndpointWithIvalidUser() throws IOException {
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users", "invalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void testDeleteEndpointWithAdminUser() throws IOException {
        //Adds user
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password", peterJSON);
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        //Deletes new user
        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    public void testPostUserWithAdmin_whenAddingNewUser() throws IOException, JSONException {
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password", peterJSON);
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
        CloseableHttpResponse getRequest = testUtils.sendGetRequest("users/8", "Gabor:password");
        JSONAssert.assertEquals(testUtils.getJsonObject(getRequest).toString(), registeredPeterJSON,
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true)));
        CloseableHttpResponse deleteRequest = testUtils.sendDeleteRequest("users/8", "Gabor:password");
    }

    @Test
    public void testPostUserWithAdmin_whenAddingExistingUser() throws IOException, JSONException { //TODO read JSONs from file
        CloseableHttpResponse postRequest = testUtils.sendPostRequest("users/register", "Gabor:password", balazsJSON);
        assertEquals(HttpStatus.SC_BAD_REQUEST, postRequest.getStatusLine().getStatusCode());
        //JSONAssert.assertEquals(testUtils.getJsonObject(postRequest).toString(), registeredPeterJSON, JSONCompareMode.LENIENT);
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


    public String balazsJSON = "{" +
            "\"username\": \"Balazs\",\n" +
            "\"password\": \"$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..\",\n" +
            "\"enabled\": true,\n" +
            "\"company\": {\n" +
            "\"id\": 1,\n" +
            "\"name\": \"TelnetWork Kft.\"\n" +
            "},\n" +
            "\"workplace\": {\n" +
            "\"id\": 1,\n" +
            "\"name\": \"TelnetWork Kft.\"\n" +
            "},\n" +
            "\"role\": \"ROLE_DIRECTOR\"\n" +
            "}";

    public String registeredPeterJSON = "{" +
            "id: 8," +
            "\"username\": \"Peter\",\n" +
            "\"password\": \"$2a$10$3TMNND3EY3k7XBwV2Iue5.I9tDB4Hv9VBHg6NrCqxC21KYb3N.1By\",\n" +
            "\"enabled\": true,\n" +
            "\"company\": {\n" +
            "\"id\": 1,\n" +
            "\"name\": \"TelnetWork Kft.\"\n" +
            "},\n" +
            "\"workplace\": {\n" +
            "\"id\": 1,\n" +
            "\"name\": \"TelnetWork Kft.\"\n" +
            "},\n" +
            "\"role\": \"ROLE_DIRECTOR\"\n" +
            "}";

    public String peterJSON = "{" +
            "\"username\": \"Peter\",\n" +
            "\"password\": \"password\",\n" +
            "\"enabled\": true,\n" +
            "\"workplace\": {\n" +
            "\"id\": 1\n" +
            "},\n" +
            "\"role\": \"ROLE_DIRECTOR\"\n" +
            "}";
}