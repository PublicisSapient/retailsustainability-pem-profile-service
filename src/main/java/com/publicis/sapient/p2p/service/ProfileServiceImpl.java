package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.external.ImageService;
import com.publicis.sapient.p2p.external.ProductService;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.repository.ProfileRepository;
import com.publicis.sapient.p2p.utils.Constants;
import com.publicis.sapient.p2p.utils.EncryptionUtil;
import com.publicis.sapient.p2p.vo.ServiceResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id : {0}";

    private static final String USER_PROFILE_NO_LONGER_EXIST = "This user profile no longer exists!";

    private static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email : {0}";

    private static final String IMAGE_SERVICE_EXCEPTION_MESSAGE = "Exception occurred while calling image-service : {0} : {1}";

    private static final String UNAUTHORIZED_ACCESS = "Unauthorized Access : Updating User for user not logged in";

    private static final  String ERR_FEIGN_CALL_MESSAGE = "Exception occurred while calling product-service";

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Value("${secret}")
    private String jwtSecret;
    
    @Autowired
    private EncryptionUtil encryptionUtil;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductService productService;

    @Override
    public ServiceResponseDto updateUserProfile(String email, UpdateUserDto updateUserDto, String tokenEmail) {
        logger.info("Entering updateUserProfile method inside ProfileServiceImpl");
        if(!tokenEmail.equals(email)){
            logger.error("Unauthorized Access : Invalid Token : Updating User Profile for wrong user");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid Token");
        }
        User user = getUserByEmail(email);

        User oldUser = new User();
        BeanUtils.copyProperties(user, oldUser);

        updateUserFields(user, updateUserDto);

        profileRepository.updateUserProfile(user);

        handleProfileImage(updateUserDto.getProfileImage(), oldUser.getProfileImage());

        return createServiceResponse(HttpStatus.OK.value(), Constants.USER_UPDATED_SUCCESSFULLY);
    }


    private User getUserByEmail(String email) {
        User user = profileRepository.getUserProfile(email);
        if (user == null) {
            logger.atWarn().log(MessageFormat.format(USER_NOT_FOUND_WITH_EMAIL, email));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(USER_NOT_FOUND_WITH_EMAIL, email));
        }
        return user;
    }

    private void updateUserFields(User user, UpdateUserDto updateUserDto) {
        User updateRequestUser = modelMapper.map(updateUserDto, User.class);
        BeanUtils.copyProperties(updateRequestUser, user, getNullPropertyNames(updateRequestUser));
    }

    private void handleProfileImage(String newProfileImage, String oldProfileImage) {
        if (newProfileImage != null) {
            removeImageFromDumpRepository(newProfileImage);
            deleteImageFromCloud(oldProfileImage);
        }
    }

    private void removeImageFromDumpRepository(String profileImage) {
        try {
            logger.info("Calling image service api /image-service/images/dumpImage delete method");
            imageService.removeImageFromDump(new UrlDto(Collections.singletonList(profileImage)));
        } catch (Exception ex) {
            logger.error(MessageFormat.format(IMAGE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
            logger.atError().log(MessageFormat.format("images not removed from dump repository : {0}", profileImage));
        }
    }

    private void deleteImageFromCloud(String profileImage) {
        if (profileImage != null) {
            try {
                logger.info("Calling image service api /image-service/images delete method");
                imageService.deleteImages(new UrlDto(Collections.singletonList(profileImage)));
            } catch (Exception ex) {
                logger.error(MessageFormat.format(IMAGE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
                logger.atError().log(MessageFormat.format("images not deleted from cloud : {0}", profileImage));
            }
        }
    }

    private ServiceResponseDto createServiceResponse(int statusCode, String message) {
        ServiceResponseDto serviceResponse = new ServiceResponseDto();
        serviceResponse.setMessage(message);
        serviceResponse.setStatusCode(statusCode);
        return serviceResponse;
    }



    @Override
    public ServiceResponseDto changeUserPassword(String email, ChangePasswordDto changePasswordDto, String tokenEmail) {
        logger.info("Entering changeUserPassword method inside ProfileServiceImpl");
        if(!tokenEmail.equals(email)){
            logger.error("Unauthorized Access : Invalid Token : Changing User Password for wrong user");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid Token");
        }
        User user = getUserByEmail(email);
        String currentPassword = encryptionUtil.decrypt(changePasswordDto.getCurrentPassword());
        String newPassword = encryptionUtil.decrypt(changePasswordDto.getNewPassword());
        if(currentPassword.equals(newPassword))
        {
            logger.error("New Password should be different from the Current Password");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "New Password should be different from the Current Password");
        }
        if(jwtUtils.validateBCryptPassword(email, changePasswordDto.getCurrentPassword())) {
            user.setPassword(encoder.encode(newPassword));

            profileRepository.updateUserProfile(user);

            return createServiceResponse(HttpStatus.OK.value(), Constants.PASSWORD_UPDATED_SUCCESSFULLY);
        } else {
            logger.warn("Current Password Do not Match with password");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Wrong Current Password");
        }
    }

    @Override
    public void deleteUserProfile(String id, CookieResponse cookieResponse) {
        logger.info("Entering deleteUserProfile method inside ProfileServiceImpl");
        if (cookieResponse.getUserId().equals(id)) {
            User user = profileRepository.getUserById(id);
            if (user != null) {
                try {
                    logger.info("Calling product service api /product-service/products/user/{userId} delete products");
                    productService.deleteProducts(id, cookieResponse.getRefreshTokenCookie().getValue());
                } catch (Exception ex) {
                    logger.error(MessageFormat.format("Not able to delete products : {0} : {1} : {2}", ERR_FEIGN_CALL_MESSAGE, ex.getClass(), ex.getMessage()));
                    throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, MessageFormat.format("Not able to delete products : {0}", "FeignException"));
                }
                profileRepository.deleteById(id);
            } else {
                logger.atWarn().log(MessageFormat.format(USER_NOT_FOUND_WITH_ID, id));
                throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(USER_NOT_FOUND_WITH_EMAIL, id));
            }
        } else {
            logger.error(UNAUTHORIZED_ACCESS);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, Constants.INVALID_JWT_TOKEN);
        }
    }

    private String[] getNullPropertyNames(Object object) {
        logger.info("Entering getNullPropertyNames method inside ProfileServiceImpl");
        final BeanWrapper wrappedSource = new BeanWrapperImpl(object);
        return Arrays.stream(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> wrappedSource.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    @Override
    public ServiceResponseDto getUserProfileById(String id, String tokenId) {
        logger.info("Entering getUserProfileByMailId method inside ProfileServiceImpl");
        if (tokenId.equals(id)) {
            ServiceResponseDto serviceResponse = new ServiceResponseDto();
            serviceResponse.setData(modelMapper.map(profileRepository.getUserById(id), UserDto.class));
            serviceResponse.setStatusCode(HttpStatus.OK.value());
            return serviceResponse;
        }else {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, Constants.INVALID_JWT_TOKEN);
        }
    }
    
    public ServiceResponse getUserProfileByMailId(String email) {
        logger.info("Entering getUserProfileByMailId method inside ProfileServiceImpl");
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setOutput(profileRepository.getUserProfile(email.toLowerCase()));
        serviceResponse.setStatusCode(HttpStatus.OK.getReasonPhrase());
        return serviceResponse;
    }

    @Override
    public ServiceResponse addUserProfile(User profile) {
        logger.info("Request received to register user profile");

        if (null != getUserProfileByMailId(profile.getEmail()).getOutput()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Registration failed. Please check the provided information and try again.");
        } else {
            profile.setPassword(encoder.encode(encryptionUtil.decrypt(profile.getPassword())));
            profile.setEmail(profile.getEmail().toLowerCase());
            profile.setCreatedTime(Timestamp.from(Instant.now()));
            profileRepository.addUserProfile(profile);
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setOutput(modelMapper.map(getUserProfileByMailId(profile.getEmail()).getOutput(), UserDto.class));
            serviceResponse.setStatusCode(HttpStatus.CREATED.getReasonPhrase());
            return serviceResponse;
        }
    }

    @Override
    public PublicUserDto getPublicUserProfile(String id) {
        logger.info("Entering getPublicUserProfile method inside ProfileServiceImpl");
        User user = profileRepository.getUserById(id);
        if(user == null) {
            logger.atWarn().log(MessageFormat.format(USER_NOT_FOUND_WITH_ID, id));
            throw new BusinessException(ErrorCode.NOT_FOUND, USER_PROFILE_NO_LONGER_EXIST);
        }
        return modelMapper.map(user, PublicUserDto.class);
    }

}

