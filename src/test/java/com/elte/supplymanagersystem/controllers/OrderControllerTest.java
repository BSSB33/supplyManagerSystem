package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.TestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;

import static com.elte.supplymanagersystem.controllers.HistoryControllerTest.historyJSONPath;
import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder
class OrderControllerTest {

    final static String orderJSONPath = "src/test/input/orders/";
    private static TestUtils testUtils = new TestUtils();

    void assertEqualJSONOrderToJSONObject(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(orderJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    void assertEqualJSONOrderArrayToJSONArray(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(orderJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    //Get All Endpoint
    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllOrdersShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONOrderArrayToJSONArray(getRequest, "allOrders.json");
    }

    @Test
    void givenNonAdminUser_whenGetAllEndpointIsCalled_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/", "Emma:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/", "ivalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get Purchases Endpoint
    @Test
    void givenDirectorUser_whenGetPurchasesEndpointIsCalled_thenAllOrdersShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/purchases", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONOrderArrayToJSONArray(getRequest, "ordersOfBalazs.json");
    }

    @Test
    void givenInvalidUser_whenGetPurchasesEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/purchases", "ivalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get Sales Endpoint
    @Test
    void givenDirectorUser_whenGetSalesEndpointIsCalled_thenAllOrdersShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/sales", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONOrderArrayToJSONArray(getRequest, "salesOfBalazs.json");
    }

    @Test
    void givenInvalidUser_whenGetSalesEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/sales", "ivalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedOrderShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/1", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONOrderToJSONObject(getRequest, "order1.json");
    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheOrderIsIssuedByOrForTheUsersCompany_thenTheRequestedOrderShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/1", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
        assertEqualJSONOrderToJSONObject(getRequest, "order1.json");
    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheOrderIsNotIssuedByOrForTheUsersCompany_thenForbiddenShouldBeThrown() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/1", "Judit:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/1", "invalidUser:password");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_withANonExistingOrder_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/100", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, getRequest.getStatusLine().getStatusCode());
    }

    //Get Histories Of Order Endpoint
    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllHistoriesOfTheOrderShouldBeReturned() throws IOException, JSONException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/1/histories", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllHistoriesOfTheOrderShouldBeReturned2() throws IOException, JSONException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("orders/1/histories", "TTManager:password");
        assertEquals(HttpStatus.SC_OK, getRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifOrderNotExists_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("orders/100/histories", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, getRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllAuthorizedHistoriesOfTheOrderShouldBeReturned() throws IOException {
        HttpResponse getRequest = testUtils.sendGetRequest("orders/1/histories", "Balazs:password");
        assertEquals(HttpStatus.SC_OK, getRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifUserIsNotAuthorizedForOrder_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse getRequest1 = testUtils.sendGetRequest("orders/1/histories", "Judit:password");
        assertEquals(HttpStatus.SC_FORBIDDEN, getRequest1.getStatusLine().getStatusCode());
    }

    //Post Histories For Order Endpoint
    @Test
    void givenAdminUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/1/histories", "Gabor:password",
                testUtils.getContentOfFile(historyJSONPath + "history1.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenUnauthorizedUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldNotBeAdded() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/1/histories", "Judit:password",
                testUtils.getContentOfFile(historyJSONPath + "history1.json"));
        assertEquals(HttpStatus.SC_FORBIDDEN, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded_AndTheCreatorShouldBeSet() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/1/histories", "Balazs:password",
                testUtils.getContentOfFile(historyJSONPath + "newHistory.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_ifUnauthorizedForOrder_thenForbiddenShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/1/histories", "Judit:password",
                testUtils.getContentOfFile(orderJSONPath + "newOrder.json"));
        assertEquals(HttpStatus.SC_FORBIDDEN, postRequest.getStatusLine().getStatusCode());
    }

    //Post Order Endpoint
    @Test
    @Order(1)
    void givenAdminUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/", "Gabor:password",
                testUtils.getContentOfFile(orderJSONPath + "newOrder.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorOrManagerUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/", "Balazs:password",
                testUtils.getContentOfFile(orderJSONPath + "newOrder2.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenPostOrderEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/", "invalidUser:password",
                testUtils.getContentOfFile(orderJSONPath + "newOrder2.json"));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postRequest.getStatusLine().getStatusCode());
    }

    //Put Order Endpoint
    @Test
    @Order(2)
    void givenAdminUser_whenUpdateOrderEndpointIsCalled_thenTheOrderShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("orders/7", "Gabor:password",
                "{\"productName\":\"Old Order\",\"price\":50000,\"status\":\"IN_STOCK\"}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        //Rollback
        HttpResponse putRequest2 = testUtils.sendPutRequest("orders/7", "Gabor:password",
                "{\"productName\":\"New Order\",\"price\":50000,\"status\":\"IN_STOCK\"}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsIssuedForHisCompany_thenTheCompanyShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("orders/6", "Balazs:password",
                "{\"productName\":\"Old Order\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        //Rollback
        HttpResponse putRequest2 = testUtils.sendPutRequest("orders/6", "Balazs:password",
                "{\"productName\":\"Office Computer\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsNotIssuedForHisCompany_thenTheCompanyShouldNotBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/4", "Balazs:password",
                "{\"productName\":\"Office Computer\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsIssuedForHisCompany_thenTheCompanyShouldBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("orders/6", "Emma:password",
                "{\"productName\":\"Old Order\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        //Rollback
        HttpResponse putRequest2 = testUtils.sendPutRequest("orders/6", "Emma:password",
                "{\"productName\":\"Office Computer\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsNotIssuedForHisCompany_thenTheCompanyShouldNotBeUpdated() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/4", "Emma:password",
                "{\"productName\":\"Office Computer\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenUpdateCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws IOException {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/2", "invalidUser:password",
                "");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putRequest1.getStatusLine().getStatusCode());
    }

    //Delete Order Endpoint
    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheOrderIsDeletable_thenTheRequestedCompanyShouldBeDeleted() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("orders/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheCompanyIsNotDeletable_thenNotAcceptableShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("orders/2", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingCompany_thenNotFoundShouldBeThrown() throws IOException {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("orders/100", "Emma:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteRequest.getStatusLine().getStatusCode());
    }
}