package com.tradeflux.user_service.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitKycRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "PAN number is required")

    @Pattern(

            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Invalid PAN number format"
    )
    private String panNumber;


    @NotBlank(message = "Aadhaar number is required")
    @Pattern(

            regexp = "^[0-9]{12}$",
            message = "Invalid Aadhaar number"
    )
    private String aadhaarNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    private String address;
}
