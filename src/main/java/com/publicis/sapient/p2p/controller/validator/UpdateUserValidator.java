package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.model.UpdateUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateUserValidator {
    private final Logger logger = LoggerFactory.getLogger(UpdateUserValidator.class);


    public void validateUpdateUserDto(UpdateUserDto updateUserDto) {
        logger.info("Entering validate method inside ProfileValidator");
        validateFirstName(updateUserDto.getFirstName());
        validateLastName(updateUserDto.getLastName());
        validatePhoneNo(updateUserDto.getPhoneno());
        validateProfileImage(updateUserDto.getProfileImage());
    }

    private void validateFirstName(String firstName) {
        logger.info("Entering validateFirstName method inside UpdateUserValidator");
        if (firstName != null && firstName.length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. firstName length must be greater than 1");
        }
    }

    private void validateLastName(String lastName) {
        logger.info("Entering validateLastName method inside UpdateUserValidator");
        if (lastName != null && lastName.length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. firstName length must be greater than 1");
        }
    }

    private void validatePhoneNo(String phoneno) {
        logger.info("Entering validatePhoneNo method inside UpdateUserValidator");
        if (phoneno != null && !phoneno.matches("^\\d{10}$")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. phoneno not valid");
        }
    }

    private void validateProfileImage(String profileImage) {
        logger.info("Entering validateProfileImage method inside UpdateUserValidator");
        List<String> content = new ArrayList<>();
        content.add("image/jpeg");
        content.add("image/png");
        if (profileImage != null && !profileImage.isEmpty()) {

            if(!profileImage.startsWith("https://storage.googleapis.com/p2p-product-images-dev/")  || !profileImage.matches("(?i).+\\.(jpg|jpeg|png)")){
                logger.error("Invalid File.");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid File.");
            }
            String contentType;
            try {
                URL url = new URL(profileImage);
                URLConnection connection = url.openConnection();
                contentType = connection.getHeaderField("Content-Type");
            } catch (Exception e) {
                logger.error("Error in reading contents of file.");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Error in reading contents of file.");
            }
            if (!content.contains(contentType)) {
                logger.error("Invalid File. Please upload an image file.");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid File. Please upload an image file.");
            }
        }
    }
}
