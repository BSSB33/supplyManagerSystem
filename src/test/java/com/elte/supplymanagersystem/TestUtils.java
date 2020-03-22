package com.elte.supplymanagersystem;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TestUtils {

    static final Logger logger = Logger.getLogger(TestUtils.class);
    static final String apiURL = "http://localhost:8080/";

    private void logRequest(String endpoint, String credentials, String typeOfRequest) {
        String loggedInUser = credentials.split(":")[0];
        logger.info("Test: " + typeOfRequest + " request to: " + apiURL + endpoint + " -> LoggedInUser: " + loggedInUser);
    }

    public HttpResponse sendGetRequest(String endpoint, String credentials) throws IOException {
        logRequest(endpoint, credentials, "GET");

        HttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(apiURL + endpoint);
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpGet.setHeader("Authorization", "Basic " + encodedCredentials);

        return client.execute(httpGet); //TODO httpGet.releaseConnection() after execute
    }

    public HttpResponse sendPostRequest(String endpoint, String credentials, String JSONToPost) throws IOException {
        logRequest(endpoint, credentials, "POST");

        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiURL + endpoint);

        StringEntity entity = new StringEntity(JSONToPost);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpPost.setHeader("Authorization", "Basic " + encodedCredentials);
        return client.execute(httpPost);
    }

    public HttpResponse sendPutRequest(String endpoint, String credentials, String JSONToPut) throws IOException {
        logRequest(endpoint, credentials, "PUT");

        HttpClient client = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(apiURL + endpoint);

        StringEntity entity = new StringEntity(JSONToPut);
        httpPut.setEntity(entity);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpPut.setHeader("Authorization", "Basic " + encodedCredentials);
        return client.execute(httpPut);
    }

    public HttpResponse sendDeleteRequest(String endpoint, String credentials) throws IOException {
        logRequest(endpoint, credentials, "DELETE");

        HttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(apiURL + endpoint);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        httpDelete.setHeader("Authorization", "Basic " + encodedCredentials);

        return client.execute(httpDelete);
    }

    public JSONArray getJsonArray(HttpResponse httpResponse) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json);
    }

    public JSONObject getJsonObject(HttpResponse httpResponse) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONObject(json);
    }

    public JSONObject getJsonArrayObject(HttpResponse httpResponse, Integer id) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONArray(json).getJSONObject(id);
    }

    public String getJsonObjectField(HttpResponse httpResponse, String field) throws IOException, JSONException {
        String json = EntityUtils.toString(httpResponse.getEntity());
        return new JSONObject(json).getString(field);
    }

    public String getJsonArrayField(HttpResponse httpResponse, Integer id, String field) throws IOException, JSONException {
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

    public CustomComparator getUserComparator(){
        return new CustomComparator(JSONCompareMode.LENIENT, new Customization("password", (o1, o2) -> true));
    }

    public CustomComparator getHistoryComparator(){
        return new CustomComparator(JSONCompareMode.LENIENT, new Customization("updatedAt", (o1, o2) -> true),
                new Customization("createdAt", (o1, o2) -> true));
    }
}
