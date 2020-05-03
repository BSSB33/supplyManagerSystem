package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.enums.HistoryType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class HistoryControllerTest {

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
    public void givenAdminUser_whenGetAllEndpointIsCalled_thenAllTheHistoriesShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(15)));
    }

    @Test
    public void givenDirectorUser_whenGetAllEndpointIsCalled_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories").with(user("Balazs").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    //Get By Id Endpoint
    @Test
    public void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedHistoryShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/1").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/5").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("5"));
    }

    @Test
    public void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheCreatorWorksAtTheSameCompany_thenTheRequestedHistoryShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/1").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/2").with(user("Emma").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"));
    }

    @Test
    public void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_ifTheCreatorDoNotWorksAtTheSameCompany_thenTheRequestedHistoryShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/12").with(user("Balazs").password("password")))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/12").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/1").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenExistingUser_whenGetByIdEndpointIsCalled_withNonExistingHistroy_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/histories/1000").with(user("Gabor").password("password")))
                .andExpect(status().isNotFound());
    }

    //Delete History Endpoint
    @Test
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheHistoryIsDeletable_thenTheRequestedHistoryShouldBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/histories/8").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    public void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingHistory_thenNotFoundShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/histories/10000").with(user("Gabor").password("password")))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    public void givenDirectorOrManagerUser_whenDeleteByIdEndpointIsCalled_withHistoryTheyCreated_thenTheRequestedHistoryShouldBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/histories/10").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    public void givenDirectorOrManagerUser_whenDeleteByIdEndpointIsCalled_withHistoryTheyDoesntCreated_thenTheRequestedHistoryShouldBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/histories/8").with(user("Gyuri").password("password")))
                .andExpect(status().isForbidden());
    }

    //Add History Endpoint
    @Test
    public void givenAdminUser_whenAddHistoryEndpointIsCalled_thenTheHistoryShouldBeAdded() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/histories/").with(user("Gabor").password("password"))
                .content(jsonToString(
                        History.builder()
                                .note("Test Note")
                                .historyType(HistoryType.NOTE)
                                .order(Order.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.note").value("Test Note"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.historyType").value("NOTE"));
    }

    @Test
    public void givenManagerUser_whenAddHistoryEndpointIsCalled_thenTheHistoryShouldBeAdded() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/histories/").with(user("Emma").password("password"))
                .content(jsonToString(
                        History.builder()
                                .note("Test Note2")
                                .historyType(HistoryType.NOTE)
                                .order(Order.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.note").value("Test Note2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.historyType").value("NOTE"));
    }

    @Test
    public void givenInvalidUser_whenRegisterHistoryEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/histories/").with(user("invalidUser").password("password"))
                .content(jsonToString(
                        History.builder()
                                .note("Test Note2")
                                .historyType(HistoryType.NOTE)
                                .order(Order.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}