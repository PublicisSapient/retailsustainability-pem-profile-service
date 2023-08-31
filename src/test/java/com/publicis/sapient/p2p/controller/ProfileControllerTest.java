package com.publicis.sapient.p2p.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.sapient.p2p.controller.validator.ProfileValidator;
import com.publicis.sapient.p2p.controller.validator.UpdateUserValidator;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.service.JwtUtils;
import com.publicis.sapient.p2p.service.ProfileService;
import com.publicis.sapient.p2p.vo.ServiceResponse;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProfileController.class})
@ExtendWith(SpringExtension.class)
class ProfileControllerTest {
    
    @Autowired
    ProfileController profileController;

    @MockBean
    ProfileService profileService;

    @MockBean
    ProfileValidator profileValidator;

    @MockBean
    UpdateUserValidator updateUserValidator;

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    JwtUtils jwtUtils;
    
    @Test
    void registerUserProfileTest() throws Exception {
        User user = new User();
        user.setId("1");
        user.setPassword("admin@123");
        user.setEmail("admin@gmail.com");
        user.setFirstName("admin");
        user.setStatus("active");
        user.setLastName("admin");

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setOutput(user);

        when(profileService.addUserProfile(any())).thenReturn(serviceResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "firstName":"user1",
                            "lastName":"test",
                            "password":"USKfXzJXyBJyjp0jZ2AMRfxc9kTNH1zDmS+xoNDrC9wMwtYvVxkXP4T10h17S6fdUThvIUJsox2EspU0gjDlYo9CoEonCiGoNIlXWPi09D0Mx7I5fTV1ZFMzW6EOzB+WBytiSlpjL39XbzG9me1axylschDsg+91X9WjEZXPMWU=",
                            "phoneno":"6785439871",
                            "email":"user1@gmail.com",
                            "status":"active"
                        }""");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(profileController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        ServiceResponse resp = new ObjectMapper().readValue(response.getContentAsString(), ServiceResponse.class);
        assertEquals(((LinkedHashMap<?, ?>)resp.getOutput()).get("email"), ((User)serviceResponse.getOutput()).getEmail());
    }

    @Test
    void updateUserProfileTest() throws Exception {
        User user = new User();
        user.setId("1");
        user.setPassword("admin@123");
        user.setEmail("a@a.com.com");
        user.setFirstName("admin");
        user.setStatus("active");
        user.setLastName("admin");

        ServiceResponseDto serviceResponse = new ServiceResponseDto();
        serviceResponse.setData(user);

        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(profileService.updateUserProfile(any(),any(),any())).thenReturn(serviceResponse);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/profile/a@a.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "firstName":"test"
                        }""");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(profileController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        ServiceResponseDto resp = new ObjectMapper().readValue(response.getContentAsString(), ServiceResponseDto.class);
        assertEquals(((LinkedHashMap<?, ?>)resp.getData()).get("firstName"), ((User)serviceResponse.getData()).getFirstName());
    }

    @Test
    void changeUserPasswordTest() throws Exception {

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("admin@123", "test@123");

        ServiceResponseDto serviceResponse = new ServiceResponseDto();
        serviceResponse.setMessage("Password Updated Successfully");

        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(profileService.changeUserPassword("a@a.com", changePasswordDto, "token")).thenReturn(serviceResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/profile/pass/a@a.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "currentPassword":"admin@123",
                            "newPassword":"test@123"
                        }""");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(profileController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

    }

    @Test
    void getUserProfileTest() throws Exception {
        User user = new User();
        user.setId("1");
        user.setPassword("admin@123");
        user.setEmail("admin@gmail.com");
        user.setFirstName("admin");
        user.setStatus("active");
        user.setLastName("admin");

        ServiceResponseDto serviceResponse = new ServiceResponseDto();
        serviceResponse.setData(user);

        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(profileService.getUserProfileById(any(),any())).thenReturn(serviceResponse);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/profile/admin@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "firstName":"rahulTest"
                        }""");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(profileController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        ServiceResponseDto resp = new ObjectMapper().readValue(response.getContentAsString(), ServiceResponseDto.class);
        assertEquals(((LinkedHashMap<?, ?>)resp.getData()).get("email"), ((User)serviceResponse.getData()).getEmail());
    }

    @Test
    void getPublicUserProfileTest() throws Exception {
        PublicUserDto publicUserDto = new PublicUserDto();
        publicUserDto.setId("1");

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setOutput(publicUserDto);

        when(profileService.getPublicUserProfile(any())).thenReturn(publicUserDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/profile/public/1")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(profileController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        ServiceResponseDto resp = new ObjectMapper().readValue(response.getContentAsString(), ServiceResponseDto.class);
        assertEquals(((LinkedHashMap<?, ?>)resp.getData()).get("id"), ((PublicUserDto)serviceResponse.getOutput()).getId());
    }

    @Test
    void deleteUserProfileTest() throws Exception {
        ServiceResponseDto serviceResponse = new ServiceResponseDto();
        serviceResponse.setMessage("Profile Deleted");

        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        CookieResponse logoutCookieResponse=new CookieResponse();
        logoutCookieResponse.setNormalCookie(new Cookie("auth","a"));
        logoutCookieResponse.setRefreshTokenCookie(new Cookie("refreshToken", "r"));
        logoutCookieResponse.setTokenCookie(new Cookie("token", "t"));


        when(jwtUtils.removeCookie()).thenReturn(logoutCookieResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/profile/1")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(profileController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto resp = new ObjectMapper().readValue(content, ServiceResponseDto.class);
        assertEquals(resp.getMessage(), serviceResponse.getMessage());

    }
    
}
