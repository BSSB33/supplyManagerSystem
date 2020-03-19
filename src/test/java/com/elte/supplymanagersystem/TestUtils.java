package com.elte.supplymanagersystem;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TestUtils {

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

    public CloseableHttpResponse sendPostRequest(String endpoint, String credentials, String JSONToPost) throws IOException {
        logRequest(endpoint, credentials, "POST");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiURL + endpoint);

        StringEntity entity = new StringEntity(JSONToPost);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpPost.setHeader("Authorization", "Basic " + encodedCredentials);
        return client.execute(httpPost);
    }

    public CloseableHttpResponse sendDeleteRequest(String endpoint, String credentials) throws IOException {
        logRequest(endpoint, credentials, "DELETE");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(apiURL + endpoint);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpDelete.setHeader("Authorization", "Basic " + encodedCredentials);

        return client.execute(httpDelete);
    }

    public JSONArray getJsonArray(CloseableHttpResponse httpResponse) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json);//TODO BROKEN?
    }

    public JSONObject getJsonObject(CloseableHttpResponse httpResponse) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONObject(json);
    }

    public JSONObject getJsonObject(CloseableHttpResponse httpResponse, Integer id) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json).getJSONObject(id);
    }

    public String getJsonField(CloseableHttpResponse httpResponse, Integer id, String field) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json).getJSONObject(id).getString(field);
    }

    public String getContentOfFile(String path) {
        StringBuilder json = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new FileReader(path));
            while (scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
