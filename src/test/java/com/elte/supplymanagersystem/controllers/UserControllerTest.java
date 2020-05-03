package com.elte.supplymanagersystem.controllers;

import com.elte.supplymanagersystem.TestUtils;
import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.HistoryType;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.enums.Status;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private String jsonToString(final Object obj) {
        try {
            ObjectMapper objectMapper =new ObjectMapper();
            objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //Get All Endpoint
    @Test
    void givenAdminUser_whenGetAllEndpointIsCalled_thenAllUsersShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenGetAllEndpointIsCalled_thenAllEmployeesShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("Judit").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("Gyuri").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @Test
    void givenManagerUser_whenGetAllEndpointIsCalled_thenAllColleaguesShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("Emma").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("TTManager").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @Test
    void givenDisabledUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("Old Student").password("password")))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenInvalidUser_whenGetAllEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    //Get By Id Endpoint
    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalled_thenTheRequestedUserShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/1").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/2").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/3").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("3"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/4").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("4"));
    }

    @Test
    void givenAdminUser_whenGetByIdEndpointIsCalledForNonExistingUserId_thenTheNotFoundShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/1000").with(user("Gabor").password("password")))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void givenManagerUser_whenGetByIdEndpointIsCalledForItselfWithoutCompanyAndWorkplace_thenTheUserShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5").with(user("Emma").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(5)
                                .username("Emma")
                                .password(encoder.encode("password"))
                                .fullName("L. Emma")
                                .email("emma@gmail.com")
                                .role(Role.ROLE_MANAGER)
                                .enabled(true)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/5").with(user("Emma").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForAnEmployee_thenTheRequestedUserShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/5").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("5"));
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForItself_thenTheRequestedUserShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/2").with(user("Balazs").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"));
    }

    @Test
    void givenDirectorUser_whenGetByIdEndpointIsCalledForAUserFromAnOtherCompany_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/1").with(user("Balazs").password("password")))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/7").with(user("Balazs").password("password")))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenManagerUser_whenGetByIdEndpointIsCalledForColleague_thenTheRequestedUserShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/2").with(user("Emma").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/4").with(user("TTManager").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("4"));
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalledForExistingUser_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/2").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenInvalidUser_whenGetByIdEndpointIsCalledForNonExistingUser_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/1000").with(user("invalidUser").password("password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenDisabledUser_whenGetByIdEndpointIsCalled_thenUnauthorizedShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/2").with(user("Old Student").password("password")))
                .andExpect(status().isUnauthorized());
    }

    //Disable Endpoint
    @Test
    void givenAdminUser_whenDisableEndpointIsCalled_toDisableAUser_thenTheUserShouldBeDisabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/3/disable").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(false));
    }

    @Test
    void givenDirectorUser_whenDisableEndpointIsCalled_toDisableAnEmployeeUser_thenTheUserShouldBeDisabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5/disable").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(false));
    }

    @Test
    void givenDirectorUser_whenDisableEndpointIsCalled_toDisableANonEmployeeUser_thenTheUserShouldNotBeDisabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/4/disable").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenManagerUser_whenDisableEndpointIsCalled_toDisableAnColleagueUser_thenTheUserShouldNotBeDisabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6/disable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/6").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(true));
    }

    @Test
    void givenManagerUser_whenDisableEndpointIsCalled_toDisableANonEmployeeUser_thenTheUserShouldNotBeDisabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6/disable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/6").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(true));
    }

    @Test
    void givenAnyUser_whenDisableEndpointIsCalled_withNonExistingUser_thenTheUserShouldNotBeEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/1000/disable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    //Enable Endpoint
    @Test
    void givenAdminUser_whenEnableEndpointIsCalled_thenTheUserShouldBeEnabled() throws Exception {
        //Prepare
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6/disable").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(false));
        //Enable
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6/enable").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(true));
    }

    @Test
    void givenDirectorUser_whenEnableEndpointIsCalled_toEnableAnEmployeeUser_thenTheUserShouldBeEnabled() throws Exception {
        //Prepare
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5/disable").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(false));
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5/enable").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(true));
    }

    @Test
    void givenDirectorUser_whenEnableEndpointIsCalled_toEnableANonEmployeeUser_thenTheUserShouldNotBeEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/4/disable").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(false));
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/4/enable").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenManagerUser_whenEnableEndpointIsCalled_toEnableABossUser_thenTheUserShouldNotBeEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/2/enable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenManagerUser_whenEnableEndpointIsCalled_toEnableAColleagueUser_thenTheUserShouldNotBeEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/2/enable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenManagerUser_whenEnableEndpointIsCalled_toEnableANonEmployeeUser_thenTheUserShouldNotBeEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6/enable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAnyUser_whenEnableEndpointIsCalled_withNonExistingUser_thenTheUserShouldNotBeEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/1000/enable").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    //Login Endpoint
    @Test
    void givenUser_whenLoginEndpointIsCalled_thenOkShouldBeReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login").with(user("Judit").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login").with(user("Emma").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //Register Endpoint
    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withManagerUserToRegister_thenUserShouldBeRegistered() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .username("newManager")
                                .password(encoder.encode("password"))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newManager"));
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withDirectorUserToRegister_thenUserShouldBeRegistered_andCompanyShouldBeSet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .username("newManager")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_DIRECTOR)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newManager"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.workplace.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.company.id").value("1"));
    }

    @Test
    void givenAdminUser_whenRegisterEndpointIsCalled_withExistingUserToRegister_thenBadRequestShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .username("Emma")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_ADMIN)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenManagerUser_whenRegisterEndpointIsCalled_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register").with(user("Emma").password("password"))
                .content(jsonToString(
                        User.builder()
                                .username("New User")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_ADMIN)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    //Put By ID Endpoint
    @Test
    void givenAnyUser_whenPutByIdEndpointIsCalled_withNonExistingUser_thenNotFoundShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/100").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(100)
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnyUser_whenPutByIdEndpointIsCalled_withEmptyUserBody_thenBadRequestShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/2").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAdminUser_whenPutByIdEndpointIsCalled_thenTheRequestedUserShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(5)
                                .username("Anna")
                                .password(encoder.encode("password"))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Anna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_MANAGER"));
    }

    @Test
    void givenAdminUser_whenPutByIdEndpointIsCalled_toModifyItself_thenTheRequestedUserShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/1").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(1)
                                .username("Gabor1")
                                .password(encoder.encode("password"))
                                .fullName("Kek Gabor")
                                .email("gabor@gmail.com")
                                .role(Role.ROLE_MANAGER)
                                .enabled(true)
                                .company(Company.builder().id(4).build())
                                .workplace(Company.builder().id(4).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Gabor1"));
    }

    @Test
    void givenAdminUser_whenPutByIdEndpointIsCalled_withADirectorWithoutWorkplace_thenTheRequestedUserShouldBeUpdatedAndCompanyShouldAppearAtWorkplaceField() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5").with(user("Gabor").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(5)
                                .username("Anna")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_DIRECTOR)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Anna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_DIRECTOR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.workplace.id").value("1"));
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toModifyAnEmployeeFromItsCompany_thenTheRequestedUserShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6").with(user("Gyuri").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(6)
                                .username("TopTradeManager")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_MANAGER)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(3).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("TopTradeManager"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_MANAGER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.workplace.id").value("3"));
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toMakeAdminOfManagerFromItsCompany_thenTheRequestedUserShouldNotBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6").with(user("Gyuri").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(6)
                                .username("TopTradeManager")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_ADMIN)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(3).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("TopTradeManager"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_MANAGER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.workplace.id").value("3"));
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toModifyItself_thenTheUserShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/2").with(user("Balazs").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(5)
                                .username("DirectorBalazs")
                                .password(encoder.encode("password"))
                                .fullName("Piros Balazs")
                                .email("balazs@gmail.com")
                                .role(Role.ROLE_DIRECTOR)
                                .enabled(true)
                                .company(Company.builder().id(1).build())
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("DirectorBalazs"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_DIRECTOR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.company.id").value("1"));
    }

    @Test
    void givenDirectorUser_whenPutByIdEndpointIsCalled_toModifyAManagerWhoWorksSomeWhereElse_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/6").with(user("Balazs").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(6)
                                .username("Anna")
                                .password(encoder.encode("password"))
                                .fullName("Test Manager")
                                .email("newmanager@gmail.com")
                                .role(Role.ROLE_MANAGER)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenManagerUser_whenPutByIdEndpointIsCalled_toModifyItself_thenTheUserShouldBeUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/5").with(user("Emma").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(5)
                                .username("ManagerEmma")
                                .password(encoder.encode("password"))
                                .fullName("Lila Emma")
                                .email("emma@gmail.com")
                                .role(Role.ROLE_DIRECTOR)
                                .enabled(true)
                                .company(null)
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("ManagerEmma"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_MANAGER")) //?
                .andExpect(MockMvcResultMatchers.jsonPath("$.workplace.id").value("1"));
    }

    @Test
    void givenManagerUser_whenPutByIdEndpointIsCalled_toModifyColleague_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/2").with(user("Emma").password("password"))
                .content(jsonToString(
                        User.builder()
                                .id(6)
                                .username("Renamed User")
                                .workplace(Company.builder().id(1).build())
                                .build()
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    //Delete Endpoint
    @Test
    void givenAdminUser_whenDeleteEndpointIsCalled_thenUserShouldBeDeleted() throws Exception {
        //Check User
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/7").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Old Student"));
        //Delete Empty User
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/7").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void givenAdminUser_whenDeleteEndpointIsCalled_toDeleteANotDeletableUser_thenNotAcceptableShouldBeThrown() throws Exception {
        //Check User
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/2").with(user("Gabor").password("password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Balazs"));
        //Delete Empty User
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/2").with(user("Gabor").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenDeleteEndpointIsCalled_toDeleteAnEmployeeUser_thenNotAcceptableShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/5").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenDeleteEndpointIsCalled_toDeleteAnNonEmployeeUser_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/3").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenDirectorUser_whenDeleteEndpointIsCalled_withNonExistingUser_thenForbiddenShouldBeThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/1000").with(user("Balazs").password("password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

}