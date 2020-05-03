package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.HistoryType;
import com.elte.supplymanagersystem.enums.Status;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    //TODO orders/histories

    @Autowired
    private MockMvc mockMvc;

    private String jsonToString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
            return objectMapper.writeValueAsString(obj);
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
    void givenNonAdminUser_whenGetAllEndpointIsCalled_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
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
    void givenInvalidUser_whenGetPurchasesEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
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
    void givenInvalidUser_whenGetSalesEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
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
    void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_withANonExistingOrder_thenNotFoundShouldBeThrown() throws Exception {
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
    void givenAdminUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifOrderNotExists_thenNotFoundShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1000/histories").with(user("TTManager").password("password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_thenAllTheAuthorizedHistoriesOfTheOrderShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1/histories").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetHistoriesByOrderIdEndpointIsCalled_ifUserIsNotAuthorizedForOrder_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/1/histories").with(user("Judit").password("password")))
                .andExpect(status().isForbidden());
    }

    //Post Histories For Order Endpoint
    @Test
    void givenAdminUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded() throws Exception {
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
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_thenTheHistoryOfTheOrderShouldBeAdded_AndTheCreatorShouldBeSet() throws Exception {
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
    void givenDirectorUser_whenPostHistoriesByOrderIdEndpointIsCalled_ifUnauthorizedForOrder_thenForbiddenShouldBeThrown() throws Exception {
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
    void givenAdminUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded() throws Exception {
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
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("TestProduct"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("3000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void givenDirectorOrManagerUser_whenPostOrderEndpointIsCalled_thenTheOrderShouldBeAdded() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/").with(user("Balazs").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .productName("TestProduct")
                                .isArchived(false)
                                .price(3000d)
                                .status(Status.CLOSED)
                                .buyer(Company.builder().id(2).build())
                                .seller(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("TestProduct"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("3000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CLOSED"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/").with(user("Emma").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .productName("TestProduct2")
                                .isArchived(false)
                                .price(4000d)
                                .status(Status.CLOSED)
                                .buyer(Company.builder().id(2).build())
                                .seller(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("TestProduct2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("4000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void givenInvalidUser_whenPostOrderEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/orders/").with(user("invalidUser").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .productName("TestProduct3")
                                .isArchived(false)
                                .price(3000d)
                                .status(Status.CLOSED)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    //Put Order Endpoint
    @Test
    @Order(2)
    void givenAdminUser_whenUpdateOrderEndpointIsCalled_thenTheOrderShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/orders/7").with(user("Gabor").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .productName("New Order")
                                .isArchived(true)
                                .price(2000d)
                                .buyerManager(User.builder().id(2).build())
                                .sellerManager(User.builder().id(1).build())
                                .buyer(Company.builder().id(1).build())
                                .seller(Company.builder().id(4).build())
                                .status(Status.SUCCESSFULLY_COMPLETED)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("New Order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("2000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESSFULLY_COMPLETED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyer.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seller.id").value("4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyerManager.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sellerManager.id").value("1"));
    }

    @Test
    void givenDirectorUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsIssuedForHisCompany_thenTheOrderShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/orders/6").with(user("Balazs").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .id(6)
                                .productName("Old Order")
                                .isArchived(false)
                                .price(110000d)
                                .status(Status.NEW)
                                .buyer(Company.builder().id(2).build())
                                .seller(Company.builder().id(1).build())
                                .buyerManager(User.builder().id(3).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Old Order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("110000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NEW"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyerManager.id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sellerManager").doesNotExist());
    }

    @Test
    void givenDirectorUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsNotIssuedForHisCompany_thenTheOrderShouldNotBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/orders/4").with(user("Balazs").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .id(4)
                                .productName("Old Order")
                                .isArchived(false)
                                .price(110000d)
                                .status(Status.NEW)
                                .buyer(Company.builder().id(2).build())
                                .seller(Company.builder().id(1).build())
                                .buyerManager(User.builder().id(3).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenManagerUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsIssuedForHisCompany_thenTheOrderShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/orders/6").with(user("Emma").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .id(6)
                                .productName("Old Order")
                                .isArchived(false)
                                .price(110000d)
                                .status(Status.NEW)
                                .buyer(Company.builder().id(2).build())
                                .seller(Company.builder().id(1).build())
                                .buyerManager(User.builder().id(3).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("Old Order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value("110000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NEW"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buyerManager.id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sellerManager").doesNotExist());
    }

    @Test
    void givenManagerUser_whenUpdateOrderEndpointIsCalled_ifTheOrderIsNotIssuedForHisCompany_thenTheOrderShouldNotBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/orders/4").with(user("Emma").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .id(4)
                                .productName("Office Computer")
                                .isArchived(false)
                                .price(110000d)
                                .status(Status.NEW)
                                .buyer(Company.builder().id(2).build())
                                .seller(Company.builder().id(1).build())
                                .buyerManager(User.builder().id(3).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenUpdateCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/orders/4").with(user("invalidUser").password("password"))
                .content(jsonToString(
                        com.elte.supplymanagersystem.entities.Order.builder()
                                .id(4)
                                .productName("Office Computer")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    //Delete Order Endpoint
    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheOrderIsDeletable_thenTheRequestedOrderShouldBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/orders/1").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void givenDirectorOrManagerUser_whenDeleteByIdEndpointIsCalled_ifTheOrderIsAssignedToHisCompany_thenTheRequestedOrderShouldBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/orders/7").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void givenDirectorOrManagerUser_whenDeleteByIdEndpointIsCalled_ifTheOrderIsNotAssignedToHisCompany_thenTheRequestedOrderShouldNotBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/orders/4").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingUser_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/orders/4").with(user("invalidUser").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingOrder_thenNotFoundShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/orders/1000").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }
}