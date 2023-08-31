package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.model.UserRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProfileValidator {
    private final Logger logger = LoggerFactory.getLogger(ProfileValidator.class);


    public void validateUserRequestDto(UserRequestDto userRequest) {
        logger.info("Entering validate method inside ProfileValidator");
        validateFirstName(userRequest.getFirstName());
        validateLastName(userRequest.getLastName());
        validateEmail(userRequest.getEmail());
        validatePassword(userRequest.getPassword());
        validatePhoneNo(userRequest.getPhoneno());
    }

    private void validateFirstName(String firstName) {
        logger.info("Entering validateFirstName method inside ProfileValidator");
        if(firstName == null)
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. firstName is mandatory");
        else if (firstName.length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. firstName length must be greater than 1");
        }
    }

    private void validateLastName(String lastName) {
        logger.info("Entering validateLastName method inside ProfileValidator");
        if(lastName == null)
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. lastName is mandatory");
        else if (lastName.length() < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. lastName length must be greater than 1");
        }
    }

    private void validateEmail(String email) {
        logger.info("Entering validateEmail method inside ProfileValidator");
        if(email == null)
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. email is mandatory");
        else if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. email not valid");
        }
    }

    private void validatePassword(String password) {
        logger.info("Entering validatePassword method inside ProfileValidator");
        if(password == null)
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. password is mandatory");
    }

    private void validatePhoneNo(String phoneno) {
        logger.info("Entering validatePhoneNo method inside ProfileValidator");
        if(phoneno == null)
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. phoneno is mandatory");
        else if (!phoneno.matches("^\\d{10}$")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. phoneno not valid");
        }
    }
}
