package com.elte.supplymanagersystem.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class StatisticsServiceTest {

    @Autowired
    private MockMvc mockMvc;

    //Monthly Income
    @Test
    public void givenAdminOrDirectorUser_whenGetMonthlyIncomeIsCalled_thenAllTheResultsShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/monthlyIncome").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.closedSaleCostPerMonth").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/monthlyIncome").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void givenManagerUser_whenGetMonthlyIncomeIsCalled_thenTheResultsShouldNotBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/monthlyIncome").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    //Monthly Expanse
    @Test
    public void givenAdminOrDirectorUser_whenGetMonthlyExpanseIsCalled_thenAllTheResultsShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/monthlyExpense").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/monthlyExpense").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void givenManagerUser_whenGetMonthlyExpanseIsCalled_thenTheResultsShouldNotBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/monthlyExpense").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    //Get Partner Statistics
    @Test
    public void givenAdminOrDirectorUser_whenGetPartnerStatisticsIsCalled_thenAllTheResultsShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/partnersStat").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/partnersStat").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void givenManagerUser_whenGetPartnerStatisticsIsCalled_thenTheResultsShouldNotBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/partnersStat").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    //Get Order Count Statistics
    @Test
    public void givenAdminOrDirectorUser_whenGetOrderCountStatisticsIsCalled_thenAllTheResultsShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/orderCount").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/orderCount").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void givenManagerUser_whenGetOrderCountStatisticsIsCalled_thenTheResultsShouldNotBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/orderCount").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    //Get User Count By Role
    @Test
    public void givenAdminUser_whenUserCountByRoleIsCalled_thenAllTheResultsShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/userCount").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void givenManagerOrDirectorUser_whenGetUserCountByRoleIsCalled_thenTheResultsShouldNotBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/userCount").with(user("Balazs").password("password")))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/orders/stats/userCount").with(user("Emma").password("password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenAnyRequest_mainPageShouldLoad() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/"))
                .andExpect(status().isOk());
    }


}
