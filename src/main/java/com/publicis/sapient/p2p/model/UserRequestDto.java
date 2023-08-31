package com.publicis.sapient.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String phoneno;

}
