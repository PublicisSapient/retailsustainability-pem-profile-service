package com.publicis.sapient.p2p.controller;

import com.publicis.sapient.p2p.controller.validator.ProfileValidator;
import com.publicis.sapient.p2p.controller.validator.UpdateUserValidator;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.service.JwtUtils;
import com.publicis.sapient.p2p.service.ProfileService;
import com.publicis.sapient.p2p.utils.Constants;
import com.publicis.sapient.p2p.vo.ServiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/profile")
@Tag(name = "Profile", description = "Profile Service API for managing user profile")
public class ProfileController {


    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileValidator profileValidator;

    @Autowired
    private UpdateUserValidator updateUserValidator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    @Operation(operationId = "registerUserProfile", description = "Register/Save User Profile", summary = "Saves the User Details from request in database", tags = {"Profile"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Profile Details Dto",
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User Logged In Successfully", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                    @ApiResponse(responseCode = "400", description = Constants.INVALID_USERID_PASSWORD_MESSAGE, content = @Content(schema = @Schema(implementation = User.class))),
            })
    public ResponseEntity<ServiceResponse> registerUserProfile(@RequestBody UserRequestDto userRequest) {
        logger.info("Entering registerUserProfile method with endpoint: /profile");
        profileValidator.validateUserRequestDto(userRequest);
        ServiceResponse serviceResponse = profileService.addUserProfile(modelMapper.map(userRequest, User.class));
        return ResponseEntity.ok(serviceResponse);
    }

    @PatchMapping(path="/{email}")
    @Operation(operationId = "updateUserProfile", description = "Updates User Profile Details", summary = "This service is used to update user details in the database", tags = {"Profile"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Profile Details Dto",
                    content = @Content(schema = @Schema(implementation = UpdateUserDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User Details", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                    @ApiResponse(responseCode = "400", description = Constants.INVALID_USERID_PASSWORD_MESSAGE, content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Invalid Jwt Token", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
            })
    public ResponseEntity<ServiceResponseDto> updateUserProfile(HttpServletRequest request, HttpServletResponse response, @PathVariable String email, @RequestBody UpdateUserDto updateUserDto) {
        logger.info("Entering updateUserProfile method with endpoint: /profile/{email}");
        var cookieResponse = jwtUtils.getTokenFromCookie(request);
        updateUserValidator.validateUpdateUserDto(updateUserDto);
        ServiceResponseDto serviceResponse = profileService.updateUserProfile(email, updateUserDto, cookieResponse.getEmail());

        response.addCookie(cookieResponse.getTokenCookie());
        response.addCookie(cookieResponse.getRefreshTokenCookie());
        response.addCookie(cookieResponse.getNormalCookie());
        return ResponseEntity.ok(serviceResponse);

    }

    @PatchMapping(path="/pass/{email}")
    @Operation(operationId = "changeUserPassword", description = "Change User Password", summary = "This service is used to update user details in the database", tags = {"Profile"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Change Password Dto",
                    content = @Content(schema = @Schema(implementation = ChangePasswordDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User Details", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = Constants.INVALID_USERID_PASSWORD_MESSAGE, content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Wrong Current Password", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
            })
    public ResponseEntity<ServiceResponseDto> changeUserPassword(HttpServletRequest request,HttpServletResponse response, @PathVariable String email, @RequestBody ChangePasswordDto changePasswordDto) {
        logger.info("Entering changeUserPassword method with endpoint: /profile/pass/{email}");
        var cookieResponse = jwtUtils.getTokenFromCookie(request);
        ServiceResponseDto serviceResponse = profileService.changeUserPassword(email, changePasswordDto, cookieResponse.getEmail());

        response.addCookie(cookieResponse.getTokenCookie());
        response.addCookie(cookieResponse.getRefreshTokenCookie());
        response.addCookie(cookieResponse.getNormalCookie());
        return ResponseEntity.ok(serviceResponse);
    }


    @GetMapping("/{id}")
    @Operation(operationId = "getUserProfile", description = "Fetches User Profile Details", summary = "This service gets the user details from the database", tags = {"Profile"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User Details", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                    @ApiResponse(responseCode = "400", description = Constants.INVALID_USERID_PASSWORD_MESSAGE, content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
            })
    public ResponseEntity<ServiceResponseDto> getUserProfile(HttpServletRequest request,HttpServletResponse response, @PathVariable("id") String id) {
        logger.info("Entering getUserProfile method with endpoint: /profile/{id}");
        var cookieResponse = jwtUtils.getTokenFromCookie(request);
        ServiceResponseDto serviceResponse = profileService.getUserProfileById(id, cookieResponse.getUserId());

        response.addCookie(cookieResponse.getTokenCookie());
        response.addCookie(cookieResponse.getRefreshTokenCookie());
        response.addCookie(cookieResponse.getNormalCookie());
        return ResponseEntity.ok(serviceResponse);
    }

    @GetMapping("/public/{id}")
    @Operation(operationId = "getPublicUserProfile", description = "Fetches Public User Profile Details", summary = "This service gets the user details from the database", tags = {"Profile"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Public User Details", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
            })
    public ServiceResponseDto getPublicUserProfile(@PathVariable("id") String id) {
        logger.info("Entering getPublicUserProfile method with endpoint: /profile/public/{id}");
        return new ServiceResponseDto(HttpStatus.OK.value(), "Profile Found Successfully", profileService.getPublicUserProfile(id));
    }

    @DeleteMapping("/{id}")
    public ServiceResponseDto deleteUserProfile(HttpServletRequest request,HttpServletResponse response, @PathVariable("id") String id) {
        logger.info("Entering deleteUserProfile method with endpoint: /profile/{id}");
        var cookieResponse = jwtUtils.getTokenFromCookie(request);
        profileService.deleteUserProfile(id, cookieResponse);
        var logoutCookieResponse = jwtUtils.removeCookie();

        response.addCookie(logoutCookieResponse.getTokenCookie());
        response.addCookie(logoutCookieResponse.getRefreshTokenCookie());
        response.addCookie(logoutCookieResponse.getNormalCookie());
        return new ServiceResponseDto(HttpStatus.OK.value(), "Profile Deleted", null);
    }
}
