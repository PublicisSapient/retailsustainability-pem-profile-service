package com.publicis.sapient.p2p.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
@Schema(name = "ResetDto", description = "Dto of the Reset password")
public class ResetDto implements Serializable {

    @Schema(type = "String", description = "email id of the User")
    private String email;
    @Schema(type = "String", description = "old password of the User")
    private String oldPassword;
    @Schema(type = "String", description = "new password of the User")
    private String newPassword;
    @Schema(type = "String", description = "confirm password of the User")
    private String confirmPassword;

}
