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

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder
class OrderControllerTest {

    final static String orderJSONPath = "src/test/input/orders/";
    private static TestUtils testUtils = new TestUtils();

    private void assertEqualJSONOrderToJSONObject(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonObject(request).toString(),
                new JSONObject(testUtils.getContentOfFile(orderJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    private void assertEqualJSONOrderArrayToJSONArray(HttpResponse request, String expectedJSONPath) throws IOException, JSONException {
        JSONAssert.assertEquals(testUtils.getJsonArray(request).toString(),
                new JSONArray(testUtils.getContentOfFile(orderJSONPath + expectedJSONPath)).toString(),
                JSONCompareMode.LENIENT);
    }

    //Get All Endpoint
    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllOrdersShouldBeReturned(){

    }

    @Test
    void givenNonAdminUser_whenGetAllEndpointIsCalled_thenForbiddenShouldBeThrown(){

    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown(){

    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedOrderShouldBeReturned(){

    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheOrderIsIssuedByOrForTheUsersCompany_thenTheRequestedOrderShouldBeReturned(){

    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheOrderIsNotIssuedByOrForTheUsersCompany_thenForbiddenShouldBeThrown(){

    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown(){

    }

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_withANonExistingOrder_thenNotFoundShouldBeThrown(){

    }

    //Get Histories Of Order Endpoint
    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllHistoriesOfTheOrderShouldBeReturned(){

    }

    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifOrderNotExists_thenNotFoundShouldBeThrown(){

    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllAuthorizedHistoriesOfTheOrderShouldBeReturned(){

    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifUserIsNotAuthorizedForOrder_thenForbiddenShouldBeThrown(){

    }

    //Post Histories For Order Endpoint
    @Test
    void givenAdminUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded(){

    }

    @Test
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded_AndTheCreatorShouldBeSet(){

    }

    @Test
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_ifUnauthorizedForOrder_thenForbiddenShouldBeThrown(){

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
    void givenAdminUser_whenPostOrderEndpointIsCalled_ifCreatorIsNull_thenTheOrderShouldBeAdded_andCreatorShouldBeSet(){

    }

    @Test
    void givenAdminUser_whenPostOrderEndpointIsCalled_ifCreatorIsNotNull_thenTheOrderShouldBeAdded(){

    }

    @Test
    void givenDirectorUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded_AndTheCreatorShouldBeSet(){

    }

    @Test
    void givenInvalidUser_whenPostOrderEndpointIsCalled_thenUnauthorizedShouldBeThrown(){

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
                "{\"productName\":\"New Order\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
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
                "{\"productName\":\"New Order\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
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