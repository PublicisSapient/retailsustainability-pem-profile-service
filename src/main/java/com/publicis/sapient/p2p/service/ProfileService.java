package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.vo.ServiceResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {

    ServiceResponseDto updateUserProfile(String email, UpdateUserDto userDto, String tokenEmail);

    ServiceResponseDto getUserProfileById(String username, String tokenEmail);

    ServiceResponse addUserProfile(User profile);

    PublicUserDto getPublicUserProfile(String id);

    ServiceResponseDto changeUserPassword(String email, ChangePasswordDto changePasswordDto, String tokenEmail);

    void deleteUserProfile(String id, CookieResponse cookieResponse);
}
