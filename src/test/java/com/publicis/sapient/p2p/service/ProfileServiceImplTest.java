package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.external.ImageService;
import com.publicis.sapient.p2p.external.ProductService;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.repository.ProfileRepository;
import com.publicis.sapient.p2p.utils.Constants;
import com.publicis.sapient.p2p.utils.EncryptionUtil;
import com.publicis.sapient.p2p.vo.ServiceResponse;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProfileServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ProfileServiceImplTest {

    @Autowired
    ProfileServiceImpl profileService;

    @MockBean
    BCryptPasswordEncoder encoder;

    @MockBean
    ProfileRepository profileRepository;
    
    @Value("${secret}")
    String jwtSecret;

    @MockBean
    EncryptionUtil encryptionUtil;
    
    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    ImageService imageService;

    @MockBean
    ProductService productService;

    @Test
    void updateUserProfileTest() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setProfileImage("profile-new.jpg");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("password");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");


        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(user);

        ServiceResponseDto response = profileService.updateUserProfile(user.getEmail(),updateUserDto, "a@a.com");

        Assertions.assertEquals(Constants.USER_UPDATED_SUCCESSFULLY, response.getMessage());
    }

    @Test
    void updateUserProfileTestWithoutUpdateProfileImage() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("password");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");


        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(user);

        ServiceResponseDto response = profileService.updateUserProfile(user.getEmail(),updateUserDto, "a@a.com");

        Assertions.assertEquals(Constants.USER_UPDATED_SUCCESSFULLY, response.getMessage());
    }

    @Test
    void updateUserProfileTestWithoutProfileImage() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setProfileImage("profile-new.jpg");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("password");
        user.setStatus("active");


        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(user);

        ServiceResponseDto response = profileService.updateUserProfile(user.getEmail(),updateUserDto, "a@a.com");

        Assertions.assertEquals(Constants.USER_UPDATED_SUCCESSFULLY, response.getMessage());
    }

    @Test
    void updateUserProfileInvalidUserTest() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setProfileImage("profile-new.jpg");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("b@a.com");
        Assertions.assertThrows(BusinessException.class, () -> profileService.updateUserProfile("a@a.com",updateUserDto, "token"));
    }

    @Test
    void updateUserProfileUserNotFoundTest() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setProfileImage("profile-new.jpg");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(null);
        Assertions.assertThrows(BusinessException.class, () -> profileService.updateUserProfile("a@a.com",updateUserDto, "token"));
    }

    @Test
    void updateUserProfileImageServiceDownTest() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setProfileImage("profile-new.jpg");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("password");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");


        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(user);
        doThrow(FeignException.class).when(imageService).removeImageFromDump(any());
        doThrow(FeignException.class).when(imageService).deleteImages(any());

        ServiceResponseDto response = profileService.updateUserProfile(user.getEmail(),updateUserDto, "a@a.com");
        Assertions.assertEquals(Constants.USER_UPDATED_SUCCESSFULLY, response.getMessage());
    }

    @Test
    void changeUserPasswordTest() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("pass", "newPass");

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(jwtUtils.validateBCryptPassword(any(), any())).thenReturn(true);
        when(encryptionUtil.decrypt(changePasswordDto.getCurrentPassword())).thenReturn("pass");
        when(encryptionUtil.decrypt(changePasswordDto.getNewPassword())).thenReturn("newPass");
        when(encoder.encode(any())).thenReturn("newPass");

        ServiceResponseDto serviceResponseDto = profileService.changeUserPassword("a@a.com", changePasswordDto, "a@a.com");
        Assertions.assertEquals(Constants.PASSWORD_UPDATED_SUCCESSFULLY, serviceResponseDto.getMessage());
    }

    @Test
    void changeUserPasswordInvalidUserTest() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("pass", "newPass");

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("b@a.com");
        Assertions.assertThrows(BusinessException.class, () -> profileService.changeUserPassword("a@a.com",changePasswordDto, "token"));
    }

    @Test
    void changeUserPasswordUserNotFoundTest() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("pass", "newPass");

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(null);
        Assertions.assertThrows(BusinessException.class, () -> profileService.changeUserPassword("a@a.com",changePasswordDto, "token"));
    }

    @Test
    void changeUserPasswordWrongPassTest() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("pass", "newPass");

        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("a@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(jwtUtils.validateBCryptPassword(any(), any())).thenReturn(false);
        when(encryptionUtil.decrypt(any())).thenReturn("newPass");
        when(encoder.encode(any())).thenReturn("newPass");

        Assertions.assertThrows(BusinessException.class, () -> profileService.changeUserPassword("a@a.com",changePasswordDto, "token"));
    }

    @Test
    void deleteUserProfileTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");
        when(jwtUtils.getUserIdFromToken(any())).thenReturn("1");
        when(profileRepository.getUserById(any())).thenReturn(user);
        CookieResponse cookieResponse=new CookieResponse();
        cookieResponse.setEmail(user.getEmail());
        cookieResponse.setUserId(user.getId());
        cookieResponse.setRefreshTokenCookie(new Cookie("a", "a"));
        Assertions.assertDoesNotThrow(() ->profileService.deleteUserProfile("1", cookieResponse));
    }

    @Test
    void deleteUserProfileFeignExceptionTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");
        when(jwtUtils.getUserIdFromToken(any())).thenReturn("1");
        when(profileRepository.getUserById(any())).thenReturn(user);
        doThrow(FeignException.class).when(productService).deleteProducts(any(), any());
        CookieResponse cookieResponse=new CookieResponse();
        cookieResponse.setEmail(user.getEmail());
        cookieResponse.setUserId(user.getId());
        cookieResponse.setRefreshToken("refreshToken");

        Assertions.assertThrows(BusinessException.class, () ->profileService.deleteUserProfile("1", cookieResponse));
    }

    @Test
    void deleteUserProfileInvalidUserTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");
        when(jwtUtils.getUserIdFromToken(any())).thenReturn("2");
        when(profileRepository.getUserById(any())).thenReturn(user);
        CookieResponse cookieResponse=new CookieResponse();
        cookieResponse.setEmail("b@b.com");
        cookieResponse.setUserId("2");
        cookieResponse.setRefreshToken("refreshToken");

        Assertions.assertThrows(BusinessException.class, () ->profileService.deleteUserProfile("1", cookieResponse));
    }

    @Test
    void deleteUserProfileUserNotFoundTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");
        when(jwtUtils.getUserIdFromToken(any())).thenReturn("1");
        when(profileRepository.getUserById(any())).thenReturn(null);
        CookieResponse cookieResponse=new CookieResponse();
        cookieResponse.setEmail(user.getEmail());
        cookieResponse.setUserId(user.getId());
        cookieResponse.setRefreshToken("refreshToken");

        Assertions.assertThrows(BusinessException.class, () ->profileService.deleteUserProfile("1", cookieResponse));
    }

    @Test
    void getUserProfileByIdTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        UserDto userDto = new UserDto();
        userDto.setId("1");

        when(profileRepository.getUserById(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(userDto);

        ServiceResponseDto serviceResponse = profileService.getUserProfileById("1", "1");

        Assertions.assertEquals(((UserDto) serviceResponse.getData()).getId(), user.getId());
    }

    @Test
    void getUserProfileByMailIdInvalidUserTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        UserDto userDto = new UserDto();
        userDto.setId("1");
        userDto.setEmail("a@a.com");
        userDto.setProfileImage("profile.jpg");

        when(jwtUtils.getUsernameFromToken(any())).thenReturn("b@a.com");
        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(userDto);

        Assertions.assertThrows(BusinessException.class, ()-> profileService.getUserProfileById("a@a.com", "token"));
    }

    @Test
    void addUserProfileTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        UserDto userDto = new UserDto();
        userDto.setId("1");
        userDto.setEmail("a@a.com");
        userDto.setProfileImage("profile.jpg");

        when(profileRepository.getUserProfile(any())).thenReturn(null).thenReturn(user);
        when(encryptionUtil.decrypt(any())).thenReturn("pass");
        when(encoder.encode(any())).thenReturn("pass");
        doNothing().when(profileRepository).addUserProfile(any());
        when(modelMapper.map(any(), any())).thenReturn(userDto);

        ServiceResponse serviceResponse = profileService.addUserProfile(user);

        Assertions.assertEquals(((UserDto) serviceResponse.getOutput()).getId(), user.getId());
    }

    @Test
    void addUserProfileUserAlreadyExistTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");


        when(profileRepository.getUserProfile(any())).thenReturn(user);

        Assertions.assertThrows(BusinessException.class, () -> profileService.addUserProfile(user));
    }

    @Test
    void getPublicUserProfileTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        PublicUserDto publicUserDto = new PublicUserDto();
        publicUserDto.setId("1");
        publicUserDto.setProfileImage("profile.jpg");

        when(profileRepository.getUserById(any())).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(publicUserDto);

        PublicUserDto result = profileService.getPublicUserProfile("1");
        Assertions.assertEquals(result.getId(), publicUserDto.getId());
    }

    @Test
    void getPublicUserProfileUserNotFoundTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        PublicUserDto publicUserDto = new PublicUserDto();
        publicUserDto.setId("1");
        publicUserDto.setProfileImage("profile.jpg");

        when(profileRepository.getUserById(any())).thenReturn(null);

        Assertions.assertThrows(BusinessException.class, () -> profileService.getPublicUserProfile("1"));
    }

    @Test
    void getUserByEmailNotFoundTest() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setProfileImage("profile-new.jpg");
        updateUserDto.setPhoneno("1234567890");
        updateUserDto.setSocialUrls(Collections.singletonList("url"));

        when(profileRepository.getUserProfile(any())).thenReturn(null);

        Assertions.assertThrows(BusinessException.class, () -> profileService.updateUserProfile("a@a.com",updateUserDto, "a@a.com"));
    }

    @Test
    void changePasswordNotMatchTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("passNotMatch", "newPass");

        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(encryptionUtil.decrypt(changePasswordDto.getCurrentPassword())).thenReturn("passNotMatch");
        when(encryptionUtil.decrypt(changePasswordDto.getNewPassword())).thenReturn("newPass");

        Assertions.assertThrows(BusinessException.class, () -> profileService.changeUserPassword("a@a.com",changePasswordDto, "a@a.com"));
    }

    @Test
    void changePasswordSameNewPasswordTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("a@a.com");
        user.setPassword("pass");
        user.setStatus("active");
        user.setProfileImage("profile.jpg");

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("pass", "pass");

        when(profileRepository.getUserProfile(any())).thenReturn(user);
        when(encryptionUtil.decrypt(changePasswordDto.getCurrentPassword())).thenReturn("pass");
        when(encryptionUtil.decrypt(changePasswordDto.getNewPassword())).thenReturn("pass");

        Assertions.assertThrows(BusinessException.class, () -> profileService.changeUserPassword("a@a.com",changePasswordDto, "a@a.com"));
    }


}
