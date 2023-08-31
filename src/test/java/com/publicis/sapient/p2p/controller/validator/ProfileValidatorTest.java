package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.model.UserRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProfileValidator.class})
@ExtendWith(SpringExtension.class)
class ProfileValidatorTest {

    @Autowired
    ProfileValidator profileValidator;

    @Test
    void validateUserRequestDtoTest() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertDoesNotThrow(() -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestFirstNameNull() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestInvalidFirstName() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("");
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestLastNameNull() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestInvalidLastName() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestEmailNull() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("test");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestInvalidEmail() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("invalidemail");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestPasswordNull() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPhoneno("0123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestPhonenoNull() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }

    @Test
    void validateUserRequestDtoTestInvalidPhoneno() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("test");
        userRequestDto.setLastName("test");
        userRequestDto.setEmail("a@a.com");
        userRequestDto.setPassword("wetgrdtgrd@#4654Cg");
        userRequestDto.setPhoneno("123456789");

        Assertions.assertThrows(BusinessException.class, () -> profileValidator.validateUserRequestDto(userRequestDto));
    }
}