package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RegistrationInDto {

    @NotNull(message = "registration date is mandatory")
    private LocalDate registrationDate;

    @NotEmpty(message = "confirmationCode is mandatory")
    private String confirmationCode;

    private boolean isPaid;

    @Min(value = 1, message = "must register at least 1 person")
    @Max(value = 5, message = "cannot register more than 5 people at once")
    private int numberOfTickets;

    @Min(value = 0, message = "amountPaid must be positive")
    private float amountPaid;

    @Min(value = 1, message = "rating must be between 1 and 5")
    @Max(value = 5, message = "rating must be between 1 and 5")
    private int rating;

    @Min(value = 1, message = "User ID must be greater than 0")
    private long userId;

    @Min(value = 1, message = "Workshop ID must be greater than 0")
    private long workshopId;
}
