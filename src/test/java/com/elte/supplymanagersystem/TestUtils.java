package com.elte.supplymanagersystem;

import com.elte.supplymanagersystem.services.OrderService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TestUtils {

    static final Logger logger = Logger.getLogger(TestUtils.class);

    private void logRequest(String endpoint, String credentials) {
        String loggedInUser = credentials.split(":")[0];
        logger.debug("Test: GetRequest to: " + "http://localhost:8080/" + endpoint + " -> LoggedInUser: " + loggedInUser);
    }

    public CloseableHttpResponse sendGetRequest(String endpoint, String credentials) throws IOException {
        logRequest(endpoint, credentials);
        HttpUriRequest request = new HttpGet("http://localhost:8080/" + endpoint);
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        request.setHeader("Authorization", "Basic " + encodedCredentials);

        return HttpClientBuilder.create().build().execute(request);//(HttpResponse) HttpClientBuilder.create().build().execute( request );
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
