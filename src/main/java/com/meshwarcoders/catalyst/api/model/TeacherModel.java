package com.meshwarcoders.catalyst.api.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity(name = "teachers")
public class TeacherModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "fullName cannot be null")
    @NotBlank(message = "fullName cannot be blank")
    private String fullName;
    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    @Email(message = "invalid email format")
    private String email;
    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, max =20, message = "password must be between 6-20 characters")
    private String password;


}
