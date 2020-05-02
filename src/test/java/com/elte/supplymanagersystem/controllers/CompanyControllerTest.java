package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerTest {

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
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllTheCompaniesShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetAllEndpointIsCalled_thenAllTheCompaniesShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies").with(user("Emma").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedCompanyShouldBeReturned() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/1").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/3").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorOrManagerUser_whenGetByIdEndpointIsCalled_thenTheRequestedCompanyShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/2").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/4").with(user("Emma").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/1").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    //My Company Endpoint
    @Test
    void givenAnyExistingUser_whenGetCompanyOfUserEndpointIsCalled_thenTheRequestedCompanyShouldBeReturned() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/mycompany").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenNewUserWithoutCompany_whenGetCompanyOfUserEndpointIsCalled_thenTheWorkplaceShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register").with(user("Balazs").password("password"))
                .content(jsonToString(
                        User.builder()
                                .username("newManager")
                                .password("$2a$04$YDiv9c./ytEGZQopFfExoOgGlJL6/o0er0K.hiGb5TGKHUL8Ebn..")
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_MANAGER)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/mycompany").with(user("newManager").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    //Delete Company Endpoint
    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheCompanyIsDeletable_thenTheRequestedCompanyShouldBeDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/5").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/companies/5").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/companies/5").with(user("Gabor").password("password")))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_ifTheCompanyIsNotDeletable_thenNotAcceptableShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/companies/2").with(user("Gabor").password("password")))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenNonAdminUser_whenDeleteByIdEndpointIsCalled_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/companies/2").with(user("Emma").password("password")))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenAdminUser_whenDeleteByIdEndpointIsCalled_withNonExistingCompany_thenNotFoundShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/companies/20").with(user("Emma").password("password")))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    //Register Company Endpoint
    @Test
    void givenAdminUser_whenRegisterCompanyEndpointIsCalled_thenTheCompanyShouldBeRegistered() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/companies/register").with(user("Gabor").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("TestCompany")
                                .active(true)
                                .address("Pázmány Péter Sétány 1/C")
                                .bankAccountNumber("NA72317747374734")
                                .taxNumber("064160131-5")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestCompany"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("Pázmány Péter Sétány 1/C"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxNumber").value("064160131-5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bankAccountNumber").value("NA72317747374734"));
    }

    @Test
    void givenNonAdminUser_whenRegisterCompanyEndpointIsCalled_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/companies/register").with(user("Emma").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("TestCompany")
                                .active(true)
                                .address("Pázmány Péter Sétány 1/C")
                                .bankAccountNumber("NA72317747374734")
                                .taxNumber("064160131-5")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenRegisterCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/companies/register").with(user("invalidUser").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("TestCompany")
                                .active(true)
                                .address("Pázmány Péter Sétány 1/C")
                                .bankAccountNumber("NA72317747374734")
                                .taxNumber("064160131-5")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    //Put Company Endpoint
    @Test
    void givenAdminUser_whenUpdateCompanyEndpointIsCalled_thenTheCompanyShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/companies/2").with(user("Gabor").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("BAV Kft.")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("BAV Kft."));
    }

    @Test
    void givenDirectorUser_whenUpdateCompanyEndpointIsCalled_ifTheCompanyIsHis_thenTheCompanyShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/companies/1").with(user("Balazs").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("TelnetWork BT.")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TelnetWork BT."));
    }

    @Test //?
    void givenDirectorUser_whenUpdateCompanyEndpointIsCalled_ifTheCompanyIsNotHis_thenTheCompanyShouldNotBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/companies/2").with(user("Balazs").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("BAV Kft.")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenManagerUser_whenUpdateCompanyEndpointIsCalled_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .put("/companies/2").with(user("Emma").password("password"))
            .content(jsonToString(
                    Company.builder()
                            .name("BAV Kft.")
                            .build()
            ))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenUpdateCompanyEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/companies/2").with(user("invalidUser").password("password"))
                .content(jsonToString(
                        Company.builder()
                                .name("BAV Kft.")
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}