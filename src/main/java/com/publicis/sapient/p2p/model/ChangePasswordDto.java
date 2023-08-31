package com.publicis.sapient.p2p.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

    @JsonProperty(namespace = "currentPassword")
    @NotBlank(message = "currentPassword is Mandatory")
    private String currentPassword;

    @JsonProperty(namespace = "newPassword")
    @NotBlank(message = "newPassword is Mandatory")
    private String newPassword;

}
