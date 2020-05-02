package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.enums.HistoryType;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static com.elte.supplymanagersystem.controllers.HistoryControllerTest.historyJSONPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String jsonToString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Get All Endpoint
    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllOrdersShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenNonAdminUser_whenGetAllEndpointIsCalled_thenForbiddenShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    //Get Purchases Endpoint
    @Test
    void givenDirectorUser_whenGetPurchasesEndpointIsCalled_thenAllOrdersShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/purchases").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenInvalidUser_whenGetPurchasesEndpointIsCalled_thenUnauthorizedShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/purchases").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    //Get Sales Endpoint
    @Test
    void givenDirectorUser_whenGetSalesEndpointIsCalled_thenAllOrdersShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/purchases").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenInvalidUser_whenGetSalesEndpointIsCalled_thenUnauthorizedShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/sales").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedOrderShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheOrderIsIssuedByOrForTheUsersCompany_thenTheRequestedOrderShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheOrderIsNotIssuedByOrForTheUsersCompany_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1").with(user("Judit").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_withANonExistingOrder_thenNotFoundShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/100").with(user("Gabor").password("password")))
                .andExpect(status().isNotFound());
    }

    //Get Histories Of Order Endpoint
    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllHistoriesOfTheOrderShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1/histories").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenTheAllHistoriesOfTheOrderShouldBeReturned2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1/histories").with(user("TTManager").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifOrderNotExists_thenNotFoundShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1000/histories").with(user("TTManager").password("password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenAllTheAuthorizedHistoriesOfTheOrderShouldBeReturned()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1/histories").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifUserIsNotAuthorizedForOrder_thenForbiddenShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1/histories").with(user("Judit").password("password")))
                .andExpect(status().isForbidden());
    }

    //Post Histories For Order Endpoint
    @Test
    void givenAdminUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/1/histories").with(user("Gabor").password("password"))
                .content(jsonToString(
                        History.builder()
                                .note("Test History Note")
                                .historyType(HistoryType.NOTE)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded_AndTheCreatorShouldBeSet()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/1/histories").with(user("Balazs").password("password"))
                .content(jsonToString(
                        History.builder()
                                .note("Test History Note2")
                                .historyType(HistoryType.NOTE)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_ifUnauthorizedForOrder_thenForbiddenShouldBeThrown()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/1/histories").with(user("Judit").password("password"))
                .content(jsonToString(
                        History.builder()
                                .note("Test History Note")
                                .historyType(HistoryType.NOTE)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    //Post Order Endpoint
    @Test
    //@Order(1)
    void givenAdminUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/").with(user("Gabor").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .productName("TestProduct")
                                .isArchived(false)
                                .price(3000d)
                                .status(Status.CLOSED)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    /*@Test
    void givenDirectorOrManagerUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded()  throws Exception {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/", "Balazs:password",
                testUtils.getContentOfFile(orderJSONPath + "newOrder2.json"));
        assertEquals(HttpStatus.SC_OK, postRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenPostOrderEndpointIsCalled_thenUnauthorizedShouldBeThrown()  throws Exception {
        HttpResponse postRequest = testUtils.sendPostRequest("orders/", "invalidUser:password",
                testUtils.getContentOfFile(orderJSONPath + "newOrder2.json"));
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postRequest.getStatusLine().getStatusCode());
    }

    //Put Order Endpoint
    @Test
    @Order(2)
    void givenAdminUser_whenUpdateOrderEndpointIsCalled_thenTheOrderShouldBeUpdated()  throws Exception {
        HttpResponse putRequest1 = testUtils.sendPutRequest("orders/7", "Gabor:password",
                "{\"productName\":\"Old Order\",\"price\":50000,\"status\":\"IN_STOCK\"}");
        assertEquals(HttpStatus.SC_OK, putRequest1.getStatusLine().getStatusCode());
        //Rollback
        HttpResponse putRequest2 = testUtils.sendPutRequest("orders/7", "Gabor:password",
                "{\"productName\":\"New Order\",\"price\":50000,\"status\":\"IN_STOCK\"}");
        assertEquals(HttpStatus.SC_OK, putRequest2.getStatusLine().getStatusCode());
    }

    @Test
    void givenDirectorUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsIssuedForHisCompany_thenTheCompanyShouldBeUpdated()  throws Exception {
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
    void givenDirectorUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsNotIssuedForHisCompany_thenTheCompanyShouldNotBeUpdated()  throws Exception {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/4", "Balazs:password",
                "{\"productName\":\"Office Computer\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenManagerUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsIssuedForHisCompany_thenTheCompanyShouldBeUpdated()  throws Exception {
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
    void givenManagerUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsNotIssuedForHisCompany_thenTheCompanyShouldNotBeUpdated()  throws Exception {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/4", "Emma:password",
                "{\"productName\":\"Office Computer\",\"price\":110000,\"status\":\"NEW\",\"buyer\"" +
                        ":{\"id\":2},\"buyerManager\":{\"id\":3},\"seller\":{\"id\":1},\"sellerManager\":null}");
        assertEquals(HttpStatus.SC_FORBIDDEN, putRequest1.getStatusLine().getStatusCode());
    }

    @Test
    void givenInvalidUser_whenUpdateCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown()  throws Exception {
        HttpResponse putRequest1 = testUtils.sendPutRequest("companies/2", "invalidUser:password",
                "");
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putRequest1.getStatusLine().getStatusCode());
    }

    //Delete Order Endpoint
    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheOrderIsDeletable_thenTheRequestedCompanyShouldBeDeleted()  throws Exception {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("orders/8", "Gabor:password");
        assertEquals(HttpStatus.SC_OK, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheCompanyIsNotDeletable_thenNotAcceptableShouldBeThrown()  throws Exception {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("orders/2", "Gabor:password");
        assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, deleteRequest.getStatusLine().getStatusCode());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingCompany_thenNotFoundShouldBeThrown()  throws Exception {
        HttpResponse deleteRequest = testUtils.sendDeleteRequest("orders/100", "Emma:password");
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteRequest.getStatusLine().getStatusCode());
    }*/
}