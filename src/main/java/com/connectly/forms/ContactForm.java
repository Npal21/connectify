package com.connectly.forms;

import org.springframework.web.multipart.MultipartFile;

import com.connectly.validators.ValidFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ContactForm {

    @NotBlank(message = "Contact Name is required")
    private String name;

    @NotBlank(message="Email is required")
    @Email(message = "Invalid Email Address [Ex: ram@gmail.com]")    
    private String email;

    @NotBlank(message="Contact Number is required")
    @Pattern(regexp="^[0-9]{10}$", message = "Invalid Phone Number")
    private String phoneNumber;

    @NotBlank(message="Contact Address is required")
    private String address;

    private String description;

    private boolean favourite;

    @Pattern(regexp = "^(http://|https://).*$", message = "Must start with http:// or https://")
    private String websiteLink;

    @Pattern(regexp = "^(http://|https://).*$", message = "Must start with http:// or https://")
    private String linkedInLink;

    //Annotation to validate image file
    //size
    //resolution
    //type

    @ValidFile(message = "Invalid file")
    private MultipartFile contactImage;

    private String picture;

}
