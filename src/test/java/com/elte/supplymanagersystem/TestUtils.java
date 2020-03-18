package com.elte.supplymanagersystem;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TestUtils {

    public String balazsJSON = "{" +
            "id: 2," +
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

    static final Logger logger = Logger.getLogger(TestUtils.class);
    static final String apiURL = "http://localhost:8080/";

    private void logRequest(String endpoint, String credentials, String typeOfRequest) {
        String loggedInUser = credentials.split(":")[0];
        logger.debug("Test: " + typeOfRequest + " request to: " + apiURL + endpoint + " -> LoggedInUser: " + loggedInUser);
    }

    public CloseableHttpResponse sendGetRequest(String endpoint, String credentials) throws IOException {
        logRequest(endpoint, credentials, "GET");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiURL + endpoint);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpGet.setHeader("Authorization", "Basic " + encodedCredentials);

        return client.execute(httpGet);
    }

    public CloseableHttpResponse sendPostRequest(String endpoint, String credentials) throws IOException {
        logRequest(endpoint, credentials, "POST");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiURL + endpoint);



        StringEntity entity = new StringEntity(balazsJSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpPost.setHeader("Authorization", "Basic " + encodedCredentials);
        return client.execute(httpPost);
    }

    public JSONArray getJsonArray(CloseableHttpResponse httpResponse) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json);
    }

    public JSONObject getJsonObject(CloseableHttpResponse httpResponse, Integer id) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json).getJSONObject(id);
    }

    public String getJSONField(CloseableHttpResponse httpResponse, Integer id, String field) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json).getJSONObject(id).getString(field);
    }
}
