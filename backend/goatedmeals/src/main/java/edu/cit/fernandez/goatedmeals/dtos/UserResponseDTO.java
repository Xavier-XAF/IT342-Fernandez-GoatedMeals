package edu.cit.fernandez.goatedmeals.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
}
