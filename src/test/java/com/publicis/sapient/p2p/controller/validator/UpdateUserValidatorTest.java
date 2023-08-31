package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.model.UpdateUserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {UpdateUserValidator.class})
@ExtendWith(SpringExtension.class)
class UpdateUserValidatorTest {

    @Autowired
    UpdateUserValidator updateUserValidator;

    @Test
    void validateUpdateUserDtoTest() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");
        updateUserDto.setPhoneno("0123456789");

        Assertions.assertDoesNotThrow(() -> updateUserValidator.validateUpdateUserDto(updateUserDto));
    }

    @Test
    void validateUpdateUserDtoTestFirstNameNull() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setLastName("test");
        updateUserDto.setPhoneno("0123456789");

        Assertions.assertDoesNotThrow(() -> updateUserValidator.validateUpdateUserDto(updateUserDto));
    }

    @Test
    void validateUpdateUserDtoTestLastNameNull() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setPhoneno("0123456789");

        Assertions.assertDoesNotThrow(() -> updateUserValidator.validateUpdateUserDto(updateUserDto));
    }

    @Test
    void validateUpdateUserDtoTestPhonenoNull() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test");
        updateUserDto.setLastName("test");

        Assertions.assertDoesNotThrow(() -> updateUserValidator.validateUpdateUserDto(updateUserDto));
    }

    @Test
    void validateUpdateUserDtoTestInvalidFirstName() {
        failureTest("", "test", "0123456789", null);
    }

    @Test
    void validateUpdateUserDtoTestInvalidLastName() {
        failureTest("test", "", "0123456789","");
    }

    @Test
    void validateUpdateUserDtoTestInvalidPhoneno() {
        failureTest("test", "test", "123456789", "https://example.com/image.jpg");
    }

    @Test
    void validateUpdateUserDtoTestInvalidProfileImage() {
        failureTest("test", "test", "0123456789", "https://example.com");
    }

    @Test
    void validateUpdateUserDtoTestInvalidProfileImageURL() {
        failureTest("test", "test", "0123456789", "string");
    }

    @Test
    void validateUpdateUserDtoTestInvalidProfileImageContentType() {
        failureTest("test", "test", "0123456789", "https://storage.googleapis.com/p2p-product-images-dev/.jpg");
    }

    void failureTest(String firstName, String lastName, String phoneno, String profileImage) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName(firstName);
        updateUserDto.setLastName(lastName);
        updateUserDto.setPhoneno(phoneno);
        updateUserDto.setProfileImage(profileImage);
        Assertions.assertThrows(BusinessException.class, () -> updateUserValidator.validateUpdateUserDto(updateUserDto));
    }

}